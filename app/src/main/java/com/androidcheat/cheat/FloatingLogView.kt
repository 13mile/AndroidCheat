package com.androidcheat.cheat

import android.app.Service
import android.content.Context
import android.graphics.Color
import android.graphics.Point
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.*
import com.androidcheat.R
import k.bs.androidcheat.model.LogLevel
import k.bs.androidcheat.prefs.Prefs
import k.bs.androidcheat.rx.mainThread
import k.bs.androidcheat.rx.onClickWithAnimation
import io.reactivex.Observable
import io.reactivex.disposables.CompositeDisposable
import io.reactivex.disposables.Disposable
import io.reactivex.rxkotlin.addTo
import kotlinx.android.synthetic.main.layout_floating_log_widget.view.*
import kr.nextm.lib.AppInstance
import kr.nextm.lib.TLog
import kr.nextm.lib.toHtml
import java.io.BufferedReader
import java.io.InputStreamReader
import java.util.concurrent.TimeUnit


class FloatingLogView(val service: Service, val params: WindowManager.LayoutParams) : FrameLayout(service) {
    private val windowManager: WindowManager
        get() = service.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private lateinit var disposable: Disposable

    private val logDisposable = CompositeDisposable()

    private var currentLogLevel = LogLevel.VERBOSE

    private var isScrollEndMode = false

    init {
        View.inflate(context, R.layout.layout_floating_log_widget, this)

        initDefault()

        initializeDragDrop()

        initSwitch()

        initSpinner()

        initializeButtons()

        initLog(LogLevel.VERBOSE)
    }

    private fun initSwitch() {
        switch__logview_scroll_end.isChecked = isScrollEndMode
        switch__logview_scroll_end.setOnCheckedChangeListener { _, b ->
            isScrollEndMode = b
        }

        switch_logview_background.isChecked = false
        switch_logview_background.setOnCheckedChangeListener { _, b ->
            if(b){
                log_view.setBackgroundColor(Color.parseColor("#ff000000"))
            }else{
                log_view.setBackgroundColor(Color.parseColor("#00000000"))
            }
        }
    }

    private fun initSpinner() {
        val elementsSpinner = listOf("VERBOSE", "DEBUG", "INFO", "WARNING", "ERROR", "FATAL")

        val adapter = ArrayAdapter(AppInstance.get(), R.layout.layout_spinner, elementsSpinner)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinner_loglevel.adapter = adapter
        spinner_loglevel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(p0: AdapterView<*>?) {

            }

            override fun onItemSelected(parent: AdapterView<*>, view: View, position: Int, id: Long) {
                initLog(LogLevel.values()[position])
            }
        }
    }

    fun initDefault() {
        title.onClickWithAnimation {
            toggleExpansionView()
        }

        scrollviewExpanded.visibility = View.GONE
        framelayoutExpanded.visibility = View.GONE
        buttons.visibility = View.GONE
        CheatCloseLayout.visibility = View.GONE
        buttonCheatClose.visibility = View.GONE
    }

    override fun onDetachedFromWindow() {
        disposable.dispose()
        super.onDetachedFromWindow()
    }

    private fun initializeDragDrop() {
        var initialX = 0
        var initialY = 0
        var initialTouchX: Float = 0.toFloat()
        var initialTouchY: Float = 0.toFloat()

        disposable = Observable.create<Point> { emitter ->
            //Drag and move floating view using user's touch action.
            root_container.setOnTouchListener(OnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {
                        //remember the initial position.
                        initialX = params.x
                        initialY = params.y

                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return@OnTouchListener true
                    }
                    MotionEvent.ACTION_UP -> {
                        val Xdiff = (event.rawX - initialTouchX).toInt()
                        val Ydiff = (event.rawY - initialTouchY).toInt()

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        return@OnTouchListener true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val Xdiff = (event.rawX - initialTouchX).toInt()
                        val Ydiff = (event.rawY - initialTouchY).toInt()

                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()

                        emitter.onNext(Point(params.x, params.y))

                        //Update the layout with new X & Y coordinate
                        windowManager.updateViewLayout(this@FloatingLogView, params)
                        return@OnTouchListener true
                    }
                }
                false
            })
        }
                .debounce(5, TimeUnit.SECONDS)
                .subscribe {
                    savePosition(it)
                }
    }

    private fun toggleExpansionView() {
        if (scrollviewExpanded.visibility == View.VISIBLE) {
            scrollviewExpanded.visibility = View.GONE
            framelayoutExpanded.visibility = View.GONE
            buttons.visibility = View.GONE
            CheatCloseLayout.visibility = View.GONE
            buttonCheatClose.visibility = View.GONE
        } else {
            scrollviewExpanded.visibility = View.VISIBLE
            framelayoutExpanded.visibility = View.VISIBLE
            buttons.visibility = View.VISIBLE
            CheatCloseLayout.visibility = View.VISIBLE
            buttonCheatClose.visibility = View.VISIBLE
        }
    }

    private fun savePosition(point: Point) {
        Prefs.save {
            it.cheat.floatingPoint = point
        }
    }


    private fun initializeButtons() {
        //Set the close button
        buttonCheatClose.setOnClickListener {
            service.stopSelf()
        }

        log_clear.onClickWithAnimation {
            clearLog()
        }

        renewal_start.onClickWithAnimation {
            initLog(currentLogLevel)
        }

        renewal_stop.onClickWithAnimation {
            logDisposable.clear()
        }
    }

    private fun initLog(logLevel: LogLevel) {
        logDisposable.clear()
        progressbar.visibility = View.VISIBLE
        log_view.text = ""

        currentLogLevel = logLevel

        Observable.interval(3L, TimeUnit.SECONDS)
                .flatMap { getLog(logLevel) }
                .mainThread()
                .doFinally {
                    progressbar.visibility = View.GONE
                }
                .subscribe({
                    log_view.text = it.toHtml()
                    if(isScrollEndMode){
                        scrollviewExpanded.fullScroll(ScrollView.FOCUS_DOWN)
                    }
                }, {
                    TLog.e(it)
                })
                .addTo(logDisposable)
    }

    private fun getLog(logLevel: LogLevel): Observable<StringBuilder> {

        /**
         * Log priority
         * Verbose < Debug < Info < Warning < Error < Fatal < Silent
         * ex) logcat *:d -d -v time
         * --> 모든태그의 우선순위 debug이상의 log time과 함께 출력후, 종료
         * */

        val log = StringBuilder()

        val process = Runtime.getRuntime().exec("logcat *:${logLevel.tag} -d -v time")
        val bufferedReader = BufferedReader(InputStreamReader(process.inputStream))

        bufferedReader.use {
            val iterator = it.lineSequence().iterator()
            while (iterator.hasNext()) {
                log.appendln(setTextColorWithLog(iterator.next()))
            }
        }

        process.destroy()

        return Observable.just(log)
    }

    private fun setTextColorWithLog(log : String): String{
        return when{
            log.contains(LogLevel.DEBUG.separator) -> "<font color=\"${LogLevel.DEBUG.textColorName}\">$log</font>"
            log.contains(LogLevel.INFO.separator) -> "<font color=\"${LogLevel.INFO.textColorName}\">$log</font>"
            log.contains(LogLevel.WARNING.separator) -> "<font color=\"${LogLevel.WARNING.textColorName}\">$log</font>"
            log.contains(LogLevel.ERROR.separator) -> "<font color=\"${LogLevel.ERROR.textColorName}\">$log</font>"
            log.contains(LogLevel.FATAL.separator) -> "<font color=\"${LogLevel.FATAL.textColorName}\">$log</font>"
            else-> "<font color=\"${LogLevel.VERBOSE.textColorName}\">$log</font>"
        }
    }

    private fun clearLog(){
        Runtime.getRuntime().exec("logcat -c")
    }

}
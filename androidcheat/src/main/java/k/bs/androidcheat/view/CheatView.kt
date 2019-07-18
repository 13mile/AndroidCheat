package k.bs.androidcheat.view

import android.app.Service
import android.content.ComponentName
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Point
import android.view.MotionEvent
import android.view.View
import android.view.View.OnTouchListener
import android.view.WindowManager
import android.widget.FrameLayout
import k.bs.androidcheat.cheat.Cheat
import k.bs.androidcheat.prefs.Prefs
import k.bs.androidcheat.util.env.Env
import k.bs.androidcheat.util.env.Ipv4Address
import io.reactivex.Observable
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import kotlinx.android.synthetic.main.layout_floating_cheat_widget.view.*
import kr.nextm.lib.AppInstance
import kr.nextm.lib.PreferencesHelper
import k.bs.androidcheat.cheat.FloatingLogViewService
import k.bs.androidcheat.BuildConfig
import k.bs.androidcheat.R
import kr.nextm.lib.TToast
import java.util.concurrent.TimeUnit


class CheatView(val service: Service, val params: WindowManager.LayoutParams) : FrameLayout(service) {
    private val windowManager: WindowManager
        get() = service.getSystemService(Context.WINDOW_SERVICE) as WindowManager

    private lateinit var disposable: Disposable

    init {
        View.inflate(context, R.layout.layout_floating_cheat_widget, this)

        setBuildClock()

        setSummary()

        setDetailText()

        layoutCheatExpanded.visibility = GONE

        initializeDragDrop()

        initializeButtons()
    }

    private fun setBuildClock() {
        if (Cheat.buildDateMillis != null)
            Observable.interval(0, 1, TimeUnit.SECONDS)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe {
                    textBuildTime.text = timeSinceBuild()

                    val color: Int = colorByTimeElapsedSinceBuild()

                    textBuildTime.setTextColor(color)
                }
        else
            textBuildTime.visibility = View.GONE
    }

    private fun timeSinceBuild(): String {
        val elapsed = elapsedSinceBuild()

        val seconds = elapsed.toInt() % 60

        val minutes = elapsed.toInt() / 60

        return minutes.toString().padStart(2, '0') + ":" + seconds.toString().padStart(2, '0')
    }

    private fun colorByTimeElapsedSinceBuild(): Int {
        val minutes = elapsedSinceBuild() / 60.0
        return when {
            minutes < 1 -> Color.WHITE
            minutes < 60 * 24 -> Color.GREEN
            minutes < 60 * 48 -> Color.YELLOW
            else -> Color.RED
        }
    }

    private fun elapsedSinceBuild(): Double {
        val diff = System.currentTimeMillis() - Cheat.buildDateMillis!!
        return diff / 1000.0
    }

    private fun setDetailText() {
        textDetails.text =
            """HELLO CHEAT WORLD!!
            IP: ${Ipv4Address.get().hostAddress}
            MODEL/SERIAL: ${Env.getModelName()}/${Env.getRawSerial()}"""
    }

    override fun onDetachedFromWindow() {
        disposable.dispose()
        super.onDetachedFromWindow()
    }

    private fun initializeDragDrop() {
        var initialX: Int = 0
        var initialY: Int = 0
        var initialTouchX: Float = 0.toFloat()
        var initialTouchY: Float = 0.toFloat()

        disposable = Observable.create<Point> { emitter ->

            var disposable: Disposable? = null
            //Drag and move floating view using user's touch action.
            root_container.setOnTouchListener(OnTouchListener { _, event ->
                when (event.action) {
                    MotionEvent.ACTION_DOWN -> {

                        disposable = Observable.timer(1, TimeUnit.SECONDS)
                            .observeOn(AndroidSchedulers.mainThread())
                            .subscribe {
                                // to SomeTing action longPress
                            }
                        //remember the initial position.
                        initialX = params.x
                        initialY = params.y

                        //get the touch location
                        initialTouchX = event.rawX
                        initialTouchY = event.rawY
                        return@OnTouchListener true
                    }
                    MotionEvent.ACTION_UP -> {
                        disposable?.dispose()

                        val Xdiff = (event.rawX - initialTouchX).toInt()
                        val Ydiff = (event.rawY - initialTouchY).toInt()

                        //The check for Xdiff <10 && YDiff< 10 because sometime elements moves a little while clicking.
                        //So that is click event.
                        if (Xdiff < 10 && Ydiff < 10) {
                            toggleExpansionView()
                        }
                        return@OnTouchListener true
                    }
                    MotionEvent.ACTION_MOVE -> {
                        val Xdiff = (event.rawX - initialTouchX).toInt()
                        val Ydiff = (event.rawY - initialTouchY).toInt()

                        if (Xdiff > 10 || Ydiff > 10) {
                            disposable?.dispose()
                        }

                        //Calculate the X and Y coordinates of the view.
                        params.x = initialX + (event.rawX - initialTouchX).toInt()
                        params.y = initialY + (event.rawY - initialTouchY).toInt()

                        emitter.onNext(Point(params.x, params.y))

                        //Update the layout with new X & Y coordinate
                        windowManager.updateViewLayout(this@CheatView, params)
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
        refreshUi()

        if (layoutCheatExpanded.visibility == View.VISIBLE) {
            layoutCheatExpanded.visibility = View.GONE
        } else {
            setDetailText()
            layoutCheatExpanded.visibility = View.VISIBLE
        }
    }

    private fun savePosition(point: Point) {
        Prefs.save {
            it.cheat.floatingPoint = point
        }
    }

    private fun setSummary() {
        if (Cheat.buildType != null) {
            textSummary.text = "${Cheat.buildType!!.capitalize()}"
            textSummary.setTextColor(Color.GREEN)
        } else
            textSummary.visibility = View.GONE

    }

    private fun initializeButtons() {

        buttonCheat.setOnClickListener {
            if (Cheat.cheatActivity == null) {
                TToast.show("require Cheat.register() call")
                return@setOnClickListener
            }

            toggleExpansionView()
            startActivity(Cheat.cheatActivity!!)
        }

        buttonRestart.setOnClickListener {
            if (Cheat.initActivity == null) {
                TToast.show("require Cheat.register() call")
                return@setOnClickListener
            }

            toggleExpansionView()
            AppInstance.restart(Cheat.initActivity!!)
        }

        buttonNewInstance.setOnClickListener {
            if (Cheat.initActivity == null) {
                TToast.show("require Cheat.register() call")
                return@setOnClickListener
            }

            startActivity(Cheat.initActivity!!)
        }

        buttonLogView.setOnClickListener {
            it.context.startService(
                Intent(
                    it.context,
                    FloatingLogViewService::class.java
                )
            )
        }

        buttonLaunchLastCheatActivity.setOnClickListener {
            toggleExpansionView()

            val lastCheatActivity = PreferencesHelper["LAST_CHEAT_ACTIVITY"]
            if (lastCheatActivity.isEmpty())
                return@setOnClickListener

            val componentName = ComponentName(
                service.packageName,
                lastCheatActivity
            )

            val intent = Intent(Intent.ACTION_MAIN)
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            intent.component = componentName
            service.startActivity(intent)
        }

        refreshUi()

        buttonCheatClose.setOnClickListener {
            service.stopSelf()
        }

        buttonOpen.setOnClickListener {
            //            startActivity(SplashActivity::class.java)
        }

    }

    private fun refreshUi() {
        buttonLaunchLastCheatActivity.text = PreferencesHelper["LAST_CHEAT_ACTIVITY", ".Nothing"].split(".").last()
    }


    private fun startActivity(clazz: Class<*>) {
        val intent = Intent(service, clazz)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        service.startActivity(intent)
    }

}
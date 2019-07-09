package k.bs.androidcheat.util

import android.os.Build
import k.bs.androidcheat.exception.ErrorLogException
import k.bs.androidcheat.type.OutputType
import com.google.gson.Gson
import com.google.gson.GsonBuilder
import com.google.gson.TypeAdapter
import com.google.gson.reflect.TypeToken
import com.google.gson.stream.JsonReader
import com.google.gson.stream.JsonWriter
import k.bs.androidcheat.type.JsonElementAs
import k.bs.androidcheat.type.JsonElementAsObject
import k.bs.androidcheat.type.JsonElementAsString
import java.lang.reflect.Type

object GsonHelper {
    private val gsonInstance: Gson by lazy {
        builder().create()
    }

    fun gson(): Gson {
        return gsonInstance
    }

    val pretty: Gson by lazy {
        builder().setPrettyPrinting()
                .create()
    }

    private fun builder(): GsonBuilder {
        return GsonBuilder().apply {
            setLenient()
            registerTypeAdapter(JsonElementAs::class.java, stringAdapter())
            registerTypeAdapter(JsonElementAsObject::class.java, stringAdapter())
            registerTypeAdapter(JsonElementAsString::class.java, stringAdapter())

            if (Build.VERSION.SDK_INT >= 27) {
                registerNotSupportedType<android.icu.util.CurrencyAmount>()
            }

            registerTypeAdapter(Int::class.java, numberConverter({ it.toInt() }))

            registerTypeAdapter(Double::class.java, numberConverter({ it.toDouble() }))

        }
    }

    private inline fun <reified T> GsonBuilder.registerNotSupportedType() {
        registerTypeAdapter(T::class.java, object : TypeAdapter<Any>() {
            override fun write(writer: JsonWriter, value: Any?) {
                throw ErrorLogException("GSON 파서가 지원하지 않는 타입 " + T::class.java.canonicalName)
            }

            override fun read(reader: JsonReader): Any {
                throw ErrorLogException("GSON 파서가 지원하지 않는 타입 " + T::class.java.canonicalName)
            }
        })
    }

    private fun <T> GsonBuilder.registerStringConvertable(typeOfClass: Class<T>, transform: (T) -> String, creator: (String) -> T) {
        registerTypeAdapter(typeOfClass, toStringConvertable(transform, creator))
    }

    private fun <T> numberConverter(creator: (String) -> T): TypeAdapter<T> = object : TypeAdapter<T>() {
        override fun write(writer: JsonWriter, value: T?) {
            value?.let {
                writer.jsonValue(it.toString())
            }
        }

        override fun read(reader: JsonReader): T {
            return try {
                creator(reader.nextString())
            } catch (t: Throwable) {
                creator("0")
            }
        }
    }

    private fun stringAdapter() = object : TypeAdapter<JsonElementAs>() {
        override fun write(writer: JsonWriter, value: JsonElementAs?) {
            value?.let {
                if (it.outputType == OutputType.ToObject) {
                    writer.jsonValue(it.toString())
                } else {
                    writer.value(it.toString())
                }
            }
        }

        override fun read(reader: JsonReader): JsonElementAs {
            throw ErrorLogException("구현 하지마시오")
        }
    }

    private fun <T> toStringConvertable(
            transform: (T) -> String,
            creator: (string: String) -> T
    ) = object : TypeAdapter<T>() {
        override fun write(writer: JsonWriter, value: T?) {
            value?.let {
                writer.value(transform(it))
            }
        }

        override fun read(reader: JsonReader): T {
            val string = reader.nextString()
            val value = creator(string)
            return value
        }
    }

    fun <T> toJson(any: T): String {
        return gson().toJson(any)
    }

    fun <T> fromJson(json: String?, clazz: Type, functionDefaultObjectCreator: (t: Throwable) -> T = { null!! }): T {
        return try {
            if (json.isNullOrEmpty()) {
                throw ErrorLogException("Cannot parse empty or null string for $clazz")
            }
            gson().fromJson(json, clazz)
        } catch (t: Throwable) {
            TLog.e(t)
            functionDefaultObjectCreator.invoke(t)
        }
    }

    inline fun <reified T> fromJson(json: String?, noinline functionDefaultObjectCreator: (t: Throwable) -> T = { t: Throwable -> throw t }): T {
        return fromJson(json, object : TypeToken<T>() {}.type, functionDefaultObjectCreator)
    }

    inline fun <T, reified R> fromObject(obj: T): R = fromJson(toJson(obj))


}
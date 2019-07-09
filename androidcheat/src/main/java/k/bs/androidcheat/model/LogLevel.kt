package k.bs.androidcheat.model

enum class LogLevel (val tag: String, val separator: String, val textColorName: String){
    VERBOSE("v", "V/", "White"),
    DEBUG("d", "D/", "Green"),
    INFO("i", "I/", "Blue"),
    WARNING("w", "W/", "Yellow"),
    ERROR("e", "E/", "Red"),
    FATAL("f", "F/", "DarkRed")
    ;
}
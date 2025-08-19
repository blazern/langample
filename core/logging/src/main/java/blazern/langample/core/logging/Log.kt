package blazern.langample.core.logging

object Log {
    private val inJunitTest: Boolean by lazy {
        try {
            Class.forName("org.junit.runner.JUnitCore")
            true
        } catch (_: ClassNotFoundException) {
            false
        }
    }
    
    fun d(tag: String, throwable: Throwable? = null, msg: ()->String) {
        if (!inJunitTest) {
            android.util.Log.d(tag, msg(), throwable)
        } else {
            println("d $tag: ${msg()}, $throwable")
        }
    }

    fun e(tag: String, throwable: Throwable? = null, msg: ()->String) {
        if (!inJunitTest) {
            android.util.Log.e(tag, msg(), throwable)
        } else {
            println("e $tag: ${msg()}, $throwable")
        }
    }

    fun i(tag: String, throwable: Throwable? = null, msg: ()->String) {
        if (!inJunitTest) {
            android.util.Log.i(tag, msg(), throwable)
        } else {
            println("i $tag: ${msg()}, $throwable")
        }
    }

    fun v(tag: String, throwable: Throwable? = null, msg: ()->String) {
        if (!inJunitTest) {
            android.util.Log.v(tag, msg(), throwable)
        } else {
            println("v $tag: ${msg()}, $throwable")
        }
    }

    fun w(tag: String, throwable: Throwable? = null, msg: ()->String) {
        if (!inJunitTest) {
            android.util.Log.w(tag, msg(), throwable)
        } else {
            println("w $tag: ${msg()}, $throwable")
        }
    }
}

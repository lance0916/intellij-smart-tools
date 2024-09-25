package lance

import com.intellij.DynamicBundle

/**
 * @author WuQinglong
 * @date 2024/9/25 09:28
 */
class SmartResource(bundle: String) : DynamicBundle(bundle) {

    companion object {
        private val resource = SmartResource("message.message")

        @JvmStatic
        fun get(key: String, vararg params: Any): String {
            return resource.getMessage(key, params)
        }
    }

}
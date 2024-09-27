package lance.tool

import cn.hutool.core.util.StrUtil
import com.intellij.openapi.ide.CopyPasteManager
import java.awt.datatransfer.StringSelection

/**
 * @author WuQinglong
 * @date 2024/9/27 08:53
 */
class SystemTool {

    companion object {

        @JvmStatic
        fun setPaste(content: String) {
            if (StrUtil.isBlank(content)) {
                return
            }
            CopyPasteManager.getInstance()
                .setContents(StringSelection(content))
        }


    }

}
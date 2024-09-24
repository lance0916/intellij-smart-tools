package lance.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.impl.welcomeScreen.WelcomeScreenUIManager
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.SearchTextField
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.treeStructure.SimpleTree
import com.intellij.util.ui.JBUI
import javax.swing.BoxLayout
import javax.swing.JPanel
import javax.swing.JTree
import javax.swing.ScrollPaneConstants
import javax.swing.tree.DefaultMutableTreeNode
import javax.swing.tree.DefaultTreeModel

/**
 * @author WuQinglong
 * @date 2024/9/24 09:51
 */
class CustomToolWindowFactory : ToolWindowFactory {

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = createPanel()
        val content = ContentFactory.SERVICE.getInstance().createContent(panel, null, false)
        toolWindow.contentManager.addContent(content)
    }

    private fun createPanel(): JPanel {
        val leftPanel = createLeftPanel()

        val rightPanel: DialogPanel = panel {
            row {
                textField()
                    .label("Name:")
            }
        }

        return OnePixelSplitter(false, 0.2f).apply {
            firstComponent = leftPanel
            secondComponent = rightPanel
        }
    }

    private fun createMainPanel(): JPanel? {
        return null
    }

    private fun createLeftPanel(): JPanel {
        // 搜索框
        val searchTextField = SearchTextField(false).apply {
            border = JBUI.Borders.customLineBottom(WelcomeScreenUIManager.getSeparatorColor())
            isEnabled = false
        }

        // 菜单
        val scrollPane = ScrollPaneFactory.createScrollPane(createMenuTree(), true).apply {
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        }

        return JPanel().apply {
            layout = BoxLayout(this, BoxLayout.Y_AXIS)
            background = WelcomeScreenUIManager.getProjectsBackground()
            isFocusTraversalPolicyProvider = true
            isFocusCycleRoot = true

            add(searchTextField)
            add(scrollPane)
        }
    }

    private fun createMenuTree(): JTree {
        val timestamp = DefaultMutableTreeNode("时间戳转换")
        val cron = DefaultMutableTreeNode("cron表达式")

        val root = DefaultMutableTreeNode("VirtualRoot").apply {
            add(timestamp)
            add(cron)
        }

        return SimpleTree(DefaultTreeModel(root)).apply {
            isRootVisible = false
        }
    }

}


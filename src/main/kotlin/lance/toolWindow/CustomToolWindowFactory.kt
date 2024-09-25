package lance.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.openapi.wm.impl.welcomeScreen.WelcomeScreenUIManager
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.SearchTextField
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.RightGap
import com.intellij.ui.dsl.builder.RowLayout
import com.intellij.ui.dsl.builder.panel
import com.intellij.ui.treeStructure.SimpleTree
import com.intellij.util.ui.JBUI
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
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

    private val splitterPanel = OnePixelSplitter(false, 0.2f)
    private val timestampPanel by lazy {
        ScrollPaneFactory.createScrollPane(createTimestampPanel(), true).apply {
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        }
    }
    private val cronPanel by lazy {
        ScrollPaneFactory.createScrollPane(createCronPanel(), true).apply {
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        }
    }

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        val panel = createPanel()
        val content = ContentFactory.SERVICE.getInstance().createContent(panel, null, false)
        toolWindow.contentManager.addContent(content)
    }

    private fun createPanel(): JPanel {
        val leftPanel = createLeftPanel()

        splitterPanel.firstComponent = leftPanel
        splitterPanel.secondComponent = timestampPanel

        return splitterPanel
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

//            add(searchTextField)
            add(scrollPane)
        }
    }

    private fun createMenuTree(): JTree {
        val timestamp = DefaultMutableTreeNode("时间戳转换")
        val cron = DefaultMutableTreeNode("Cron表达式")

        val root = DefaultMutableTreeNode("VirtualRoot").apply {
            add(timestamp)
            add(cron)
        }

        return SimpleTree(DefaultTreeModel(root)).apply {
            isRootVisible = false
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (e.clickCount != 2) {
                        return
                    }

                    val selectionModel = (e.source as SimpleTree).selectionModel
                    val lastPathComponent = selectionModel.selectionPath?.lastPathComponent ?: return

                    val userObject = (lastPathComponent as DefaultMutableTreeNode).userObject
                    if (userObject == "时间戳转换") {
                        splitterPanel.secondComponent = timestampPanel
                    } else if (userObject == "Cron表达式") {
                        splitterPanel.secondComponent = cronPanel
                    }
                    splitterPanel.updateUI()
                }
            })
        }
    }

    private fun createTimestampPanel(): JPanel {
        println("createTimestampPanel")
        return panel {
            group("Current timestamp") {
                row {
                    textField()
                        .gap(RightGap.SMALL)
                        .enabled(false)
                    comboBox(listOf("Second", "Millisecond"))
                }

                row {
                    button("Refresh") {
                        println("Click Refresh Button")
                    }
                    button("Copy") {
                        println("Click Copy Button")
                    }
                }
            }

            groupRowsRange("Timestamp To Datetime") {
                row {
                    label("Timestamp:")
                    textField()
                        .gap(RightGap.SMALL)
                    comboBox(listOf("Second", "Millisecond"))
                    comboBox(listOf("ShangHai", "Beijing"))
                }.layout(RowLayout.PARENT_GRID)

                row {
                    label("Result:")
                    textField()
                }.layout(RowLayout.PARENT_GRID)

                row {
                    label("")
                    button("Convert") {
                        println("Click Convert Button")
                    }
                }.layout(RowLayout.PARENT_GRID)
            }

            groupRowsRange("Datetime To Timestamp") {
                row {
                    label("Datetime:")
                    textField()
                        .gap(RightGap.SMALL)
                    comboBox(listOf("Second", "Millisecond"))
                    comboBox(listOf("ShangHai", "Beijing"))
                }.layout(RowLayout.PARENT_GRID)

                row {
                    label("Result:")
                    textField()
                }.layout(RowLayout.PARENT_GRID)

                row {
                    label("")
                    button("Convert") {
                        println("Click Convert Button")
                    }
                }.layout(RowLayout.PARENT_GRID)

            }

        }.withBorder(JBUI.Borders.empty(10))
    }

    private fun createCronPanel(): JPanel {
        println("createCronPanel")
        return panel {
            row {
                textField()
                    .label("Cron")
            }
        }.withBorder(JBUI.Borders.empty(10))
    }

}


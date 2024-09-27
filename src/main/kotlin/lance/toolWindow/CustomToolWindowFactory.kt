package lance.toolWindow

import com.intellij.openapi.project.Project
import com.intellij.openapi.ui.ComboBox
import com.intellij.openapi.ui.DialogPanel
import com.intellij.openapi.ui.naturalSorted
import com.intellij.openapi.wm.ToolWindow
import com.intellij.openapi.wm.ToolWindowFactory
import com.intellij.ui.CollectionComboBoxModel
import com.intellij.ui.OnePixelSplitter
import com.intellij.ui.ScrollPaneFactory
import com.intellij.ui.SearchTextField
import com.intellij.ui.components.JBList
import com.intellij.ui.components.JBTextField
import com.intellij.ui.content.ContentFactory
import com.intellij.ui.dsl.builder.*
import com.intellij.ui.dsl.gridLayout.HorizontalAlign
import com.intellij.util.ui.JBUI
import com.jetbrains.rd.util.getOrCreate
import kotlinx.datetime.TimeZone
import lance.tool.DateTool
import lance.tool.SystemTool
import java.awt.event.MouseAdapter
import java.awt.event.MouseEvent
import javax.swing.JPanel
import javax.swing.JScrollPane
import javax.swing.ScrollPaneConstants

/**
 * @author WuQinglong
 * @date 2024/9/24 09:51
 */
class CustomToolWindowFactory : ToolWindowFactory {

    private val second = "Second"
    private val millisecond = "Millisecond"
    private val availableZoneIds = TimeZone.availableZoneIds.toList().naturalSorted()
    private val dateTool = DateTool()
    private val panelCache = mutableMapOf<String, JScrollPane>()
    private val splitterPanel = OnePixelSplitter(false, 0.2f)

    override fun createToolWindowContent(project: Project, toolWindow: ToolWindow) {
        splitterPanel.firstComponent = createLeftPanel()
        splitterPanel.secondComponent = panelCache.getOrCreate("时间戳转换") { k -> getMenuPanel(k) }

        val content = ContentFactory.SERVICE.getInstance().createContent(splitterPanel, null, false)
        toolWindow.contentManager.addContent(content)
    }

    private fun getMenuPanel(menu: String): JScrollPane {
        var panel: DialogPanel = when (menu) {
            "时间戳转换" -> createTimestampPanel()
            "Cron表达式" -> createCronPanel()
            else -> DialogPanel()
        }
        return ScrollPaneFactory.createScrollPane(panel, true).apply {
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
        }
    }

    private fun createLeftPanel(): JPanel {
        // 搜索框
        val searchTextField = SearchTextField(false)

        // 菜单
        val menuList = JBList("时间戳转换").apply {
            addMouseListener(object : MouseAdapter() {
                override fun mouseClicked(e: MouseEvent) {
                    if (e.clickCount != 2) {
                        return
                    }
                    val value = (e.source as JBList<*>).selectedValue as String
                    val panel = panelCache.getOrCreate(value) { k -> getMenuPanel(k) }
                    if (panel === splitterPanel.secondComponent) {
                        return
                    }
                    splitterPanel.secondComponent = panelCache.getOrCreate(value) { k -> getMenuPanel(k) }
                    splitterPanel.updateUI()
                }
            })
        }
        val scrollPane = ScrollPaneFactory.createScrollPane(menuList, true).apply {
            horizontalScrollBarPolicy = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER
            border = JBUI.Borders.empty()
        }

        return panel {
            row {
                cell(searchTextField)
                    .horizontalAlign(HorizontalAlign.FILL)
                    .enabled(false)
            }
            separator()
            row {
                cell(scrollPane)
                    .horizontalAlign(HorizontalAlign.FILL)
            }
        }
    }

    private fun createTimestampPanel(): DialogPanel {
        val currentField = JBTextField()
        val timestampField = JBTextField()
        val timestampResultField = JBTextField()
        val datetimeField = JBTextField()
        val datetimeSecondField = JBTextField()
        val datetimeMillisecondField = JBTextField()

        val timeComboBox = ComboBox(CollectionComboBoxModel(listOf(millisecond, second)))
        val availableZoneIdsComboBox1 = ComboBox(CollectionComboBoxModel(availableZoneIds))
        val availableZoneIdsComboBox2 = ComboBox(CollectionComboBoxModel(availableZoneIds))

        availableZoneIdsComboBox1.selectedItem = TimeZone.currentSystemDefault().id
        availableZoneIdsComboBox2.selectedItem = TimeZone.currentSystemDefault().id

        return panel {
            group("Current timestamp") {
                row {
                    cell(currentField)
                        .gap(RightGap.SMALL)
                        .enabled(false)
                        .columns(COLUMNS_SHORT)
                        .text(System.currentTimeMillis().toString())
                    cell(timeComboBox)
                }

                row {
                    button("Refresh") {
                        var value = System.currentTimeMillis().toString()
                        val format = timeComboBox.selectedItem as String
                        if (format == second) {
                            value = value.substring(0, value.length - 3)
                        }
                        currentField.text = value
                        currentField.repaint()
                    }
                    button("Copy") {
                        SystemTool.setPaste(currentField.text)
                    }
                }
            }

            groupRowsRange("Timestamp To Datetime") {
                row {
                    label("Timestamp:")
                    cell(timestampField)
                        .gap(RightGap.SMALL)
                        .columns(COLUMNS_SHORT)
                    cell(availableZoneIdsComboBox1)
                }.layout(RowLayout.PARENT_GRID)

                row {
                    label("Result:")
                    cell(timestampResultField)
                        .columns(COLUMNS_SHORT)
                    button("Copy") {
                        SystemTool.setPaste(timestampResultField.text)
                    }
                }.layout(RowLayout.PARENT_GRID)

                row {
                    label("")
                    button("Convert") {
                        val timestamp = timestampField.text
                        val zoneId = availableZoneIdsComboBox1.selectedItem as String
                        timestampResultField.text = dateTool.timestampToDateTime(timestamp, zoneId)
                        timestampResultField.repaint()
                    }
                }.layout(RowLayout.PARENT_GRID)
            }

            groupRowsRange("Datetime To Timestamp") {
                row {
                    label("Datetime:")
                    cell(datetimeField)
                        .gap(RightGap.SMALL)
                        .columns(COLUMNS_SHORT)
                    cell(availableZoneIdsComboBox2)
                }.layout(RowLayout.PARENT_GRID)

                row {
                    label("Result:")
                    cell(datetimeSecondField)
                        .gap(RightGap.SMALL)
                        .columns(COLUMNS_SHORT)
                    label("Second")
                    button("Copy") {
                        SystemTool.setPaste(datetimeSecondField.text)
                    }
                }.layout(RowLayout.PARENT_GRID)

                row {
                    label("")
                    cell(datetimeMillisecondField)
                        .gap(RightGap.SMALL)
                        .columns(COLUMNS_SHORT)
                    label("Millisecond")
                    button("Copy") {
                        SystemTool.setPaste(datetimeMillisecondField.text)
                    }
                }.layout(RowLayout.PARENT_GRID)

                row {
                    label("")
                    button("Convert") {
                        val datetime = datetimeField.text
                        val zoneId = availableZoneIdsComboBox2.selectedItem as String
                        val pair = dateTool.dateTimeToTimestamp(datetime, zoneId)
                        datetimeSecondField.text = pair.first
                        datetimeMillisecondField.text = pair.second
                        datetimeSecondField.repaint()
                        datetimeMillisecondField.repaint()
                    }
                }.layout(RowLayout.PARENT_GRID)
            }

        }.withBorder(JBUI.Borders.empty(10))
    }

    private fun createCronPanel(): DialogPanel {
        return panel {
            group("Cron Expression") {
                row {
                    label("Cron")
                    textField()
                    button("Click") {

                    }
                }

                row {
                    label("Next at:")
                }
            }
        }.withBorder(JBUI.Borders.empty(10))
    }

}


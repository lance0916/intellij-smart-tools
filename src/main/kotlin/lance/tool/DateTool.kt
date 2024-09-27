package lance.tool

import cn.hutool.core.date.CalendarUtil
import cn.hutool.core.date.DatePattern
import cn.hutool.core.date.DateUtil
import cn.hutool.core.util.NumberUtil
import cn.hutool.core.util.StrUtil
import java.time.ZoneId
import java.util.*

/**
 * @author WuQinglong
 * @date 2024/9/26 13:53
 */
class DateTool {

    /**
     * 时间戳转日期时间
     */
    fun timestampToDateTime(timestamp: String, zoneId: String): String {
        if (StrUtil.isBlank(timestamp) && !StrUtil.isNumeric(timestamp)) {
            return ""
        }
        if (timestamp.length != 10 && timestamp.length != 13) {
            return ""
        }

        var time: Long = 0
        if (timestamp.length == 10) {
            time = NumberUtil.parseLong(timestamp + "000")
        } else {
            time = NumberUtil.parseLong(timestamp)
        }

        val timeZone = TimeZone.getTimeZone(zoneId)
        val calendar = CalendarUtil.calendar(time, timeZone)
        return DateUtil.format(calendar.time, DatePattern.NORM_DATETIME_PATTERN)
    }

    /**
     * 日期时间转时间戳
     */
    fun dateTimeToTimestamp(datetime: CharSequence, zoneId: String): Pair<String, String> {
        try {
            val localDateTime = DateUtil.parse(datetime).toLocalDateTime()
            val zonedDateTime = localDateTime.atZone(ZoneId.of(zoneId))

            val second = zonedDateTime.toEpochSecond()
            val milli = zonedDateTime.toInstant().toEpochMilli()
            return Pair("$second", "$milli")
        } catch (_: Error) {
            // ignore
        }
        return Pair("", "")
    }

}
package entity

import java.io.Serializable

class TrainingList : Serializable {
    var Id: String? = null //培训记录Id
    var Title: String? = null //培训标题
    var CategoryId: Int = -1//培训类型Id
    var SignUpDeadline: String? = null//报名截至时间
    var StartTime: String? = null//开始时间
    var EndTime: String? = null//结束时间
    var Site: String? = null//培训地点
    var InsertTime: String? = null//记录创建时间
    var UpdateTime: String? = null//记录更新时间
    var CategoryTitle: String? = null//培训类型标题
    var ProjectTitles: String? = null//培训项目，多个时英文逗号间隔
    var TotalSignUp: Int = -1//报名人数
    var LecturerNames: String? = null//讲师名称，多个时英文逗号间隔
    var Limitations: String? = null//人员限定
    var State: Int = -1//状态，0为报名中，1为已截止，2为进行中，3为已结束
    var IsRed: Boolean = true // 是否第一次阅读
}
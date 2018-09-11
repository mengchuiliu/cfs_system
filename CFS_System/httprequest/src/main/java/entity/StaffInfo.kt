package entity

import java.io.Serializable

class StaffInfo : Serializable {
    @JvmField()
    var ID: Int = -1//员工培训记录主键ID
    @JvmField()
    var RecordId: Int = -1//培训ID
    @JvmField()
    var UserId: Int = -1//用户ID
    @JvmField()
    var UserName: String? = null//员工姓名
    @JvmField()
    var EmployeeNo: String? = null//员工编号
    @JvmField()
    var UserCardNo: String? = null//员工身份证号码
    @JvmField()
    var UserCompanyId: String? = null//员工所在门店id
    @JvmField()
    var UserPosition: Int? = null//员工职位id
    @JvmField()
    var UserZoneId: String? = null//员工所在城市id
    @JvmField()
    var ProjectIds: String? = null//员工报名项目ID，用英文,连接
    @JvmField()
    var ProjectNames: String? = null//员工报名项目名称，用英文,连接
    @JvmField()
    var IsPass: Int = -3//是否通过，0-未设置，1-已通过，2-未通过
    @JvmField()
    var Integral: String? = null//积分
    @JvmField()
    var Honor: String? = null//荣耀
    @JvmField()
    var Achievement: String? = null//成绩
    @JvmField()
    var Cost: String? = null//费用
    @JvmField()
    var Operator: Int = -1//报名操作人id
    @JvmField()
    var SignUpTime: String? = null//报名操作时间
    @JvmField()
    var State: Int = -1//状态 1已报名 2已截止报名 3已参加 4已报名未参加
    @JvmField()
    var DelMarker: Boolean = false
    @JvmField()
    var InsertTime: String? = null
    @JvmField()
    var UpdateTime: String? = null
    @JvmField()
    var UserPositionName: String? = null//员工职位
    @JvmField()
    var IsGreatOperation: Boolean? = null//报名用户 是否能剧烈运动
    @JvmField()
    var IsNeedCheckInBefore: Boolean? = null//报名用户 是否需要培训前一天入住
    @JvmField()
    var IsNeedUnifiedRiding: Boolean? = null//报名用户 是否需要统一乘车
    @JvmField()
    var IsTimeOverlap: Boolean = false//true ->时间冲突不可选培训人员 false->可选人员
}
package entity

import java.io.Serializable

class BorrowCustomer : Serializable {
    var CustomerId: Int = -1 //顾客Id
    var IdCardState: String? = null//身份证地址(省)编号
    var IdCardCity: String? = null//身份证地址(市)编号
    var IdCardDetail: String? = null//身份证详细地址
    var Race: String? = null//民族
    var RaceName: String? = null//民族
    var AbodeState: String? = null//居住地址（省）编号
    var AbodeCity: String? = null//居住地址（市）编号
    var EmpDepartment: String? = null//部门
    var EmpPost: String? = null//职位
    var MonthIncome: String? = null//月收入
    var EmpPhone: String? = null//单位电话
    var EmpState: String? = null//单位地址（省）编号
    var EmpCity: String? = null//单位地址（市）编号
    var EmpDetail: String? = null//单位详细地址
    var HouseState: String? = null//房产地址（省）编号
    var HouseCity: String? = null//房产地址（市）编号
    var HouseDetailAddr: String? = null//房产详细地址
    var HouseArea: String? = null//房屋面积
    var HousePropertyRatio: String? = null//产权比例
    var DelMarker: Boolean = false
    var InsertTime: String? = null//记录创建时间
    var UpdateTime: String? = null//记录更新时间
    var IdCardStateName: String? = null//身份证地址(省)名
    var IdCardCityName: String? = null//身份证地址(市)名
    var AbodeStateName: String? = null//居住地址（省）名
    var AbodeCityName: String? = null//居住地址（市）名
    var EmpStateName: String? = null//单位地址（省）名
    var EmpCityName: String? = null//单位地址（市）名
    var HouseStateName: String? = null//房产地址（省）名
    var HouseCityName: String? = null//房产地址（市）名
    var Name: String? = null//顾客姓名
    var Gender: Int = -1//性别，0-女，1-男
    var Phone: String? = null//手机号码
    var IdCardNumber: String? = null//身份证号码
    var AbodeDetail: String? = null//居住详细地址
    var Qualification: Int = -1//学历，枚举见下
    var MaritalStatus: Int = -1//婚姻情况，枚举见下
    var EmpName: String? = null//单位名称
    var ContactName1: String? = null//联系人1姓名
    var ContactRelation1: Int = -1//联系人1关系，枚举见下
    var ContactMobile1: String? = null//联系人1手机号码
    var ContactName2: String? = null//联系人2姓名
    var ContactRelation2: Int = -1//联系人2关系，枚举见下
    var ContactMobile2: String? = null//联系人2姓名
}
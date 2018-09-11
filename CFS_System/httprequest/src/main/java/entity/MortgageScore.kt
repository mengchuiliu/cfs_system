package entity

/**
 * Created by Administrator on 2018/3/8.
 */
class MortgageScore {
    var loanCode: String = ""
    var name: String = ""
    var userID: String = ""
    var score: Float = 0f
    var scoreType: String = ""//积分类型
    var scoreDescribe: String = ""
    var servicePeople: String? = null //记录者
    var remark: String? = null
    var updateTime: String? = null
    var oneStar: Int = 0
    var twoStar: Int = 0
    var threeStar: Int = 0
    var fourStar: Int = 0
    var fiveStar: Int = 0
}
package entity

/**
 * Created by Administrator on 2018/3/13.
 */
class CityInfo {
    /**
     * Types : 1
     * Codes : 120
     * ParentCode : 0x00
     * Names : 天津市
     * Children : [{"Types":2,"Codes":"1100","ParentCode":"120","Names":"天津市","Children":null}]
     */
    var Types: Int = 0
    var Codes: String? = null
    var ParentCode: String? = null
    var Names: String? = null
    var Children: MutableList<ChildrenBean>? = null

    class ChildrenBean {
        /**
         * Types : 2
         * Codes : 1100
         * ParentCode : 120
         * Names : 天津市
         * Children : null
         */
        var Types: Int = 0
        var Codes: String? = null
        var ParentCode: String? = null
        var Names: String? = null
        var Children: Any? = null
    }
}
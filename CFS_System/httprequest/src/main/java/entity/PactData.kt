package entity

//合同文件
class PactData {
    var ID: String? = null //主键ID
    var fileGuid: String? = null//文件id
    var customer: String? = null //客户姓名
    var fileType: String? = null //文件类型
    var fileName: String? = null //文件名称
    var fileSize: String? = null//文件大小
    var uploader: String? = null//上传者
    var uploandTime: String? = null//上传时间
    var viewTime: String? = null//查看时间
    var remark: String? = null//备注
    var reason: String? = null//不合规原因
    var contractID: Int? = 0//合同id
    var isCurAudit = false
}
package entity

/**
 *
 * 回款审核，附件数据结构
 * @author huangdongqiang
 * @date 02/07/2018
 */
class ReturnedAttachmentEntity {
    var Id: Int = 0
    var ContractID: String = ""     //所属协议
    var BookID: String = ""         //回款列表Id
    var InsertTime: String = ""     //上传时间
    var LP1: Int = 0                //文件类型1:png
}
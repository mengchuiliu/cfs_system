package com.xxjr.cfs_system.tools;

import android.os.Environment;

/**
 * Created by Administrator on 2017/3/13.
 *
 * @author mengchuiliu
 */

public class Constants {
    // 正常状态
    public static final int APP_STATE_NORMAL = 0;
    // 从后台回到前台
    public static final int APP_STATE_BACK_TO_FRONT = 1;
    // 从前台进入后台
    public static final int APP_STATE_FRONT_TO_BACK = 2;
    // 后台回到前台间隔时间
    public static final long IntervalTime = 0;

    //上传最多图片数
    public static final int MAX_IMAGE_SIZE = 6;

    public static final int MANAGER_CODE = 1000;//客户经理
    public static final int CARD_CODE = 1001;//证件类型
    public static final int BANK_CODE = 1002;//银行
    public static final int BANK_PRODUCT_CODE = 1003;//银行产品
    public static final int MORTGAGE_CODE = 1004;//按揭员
    public static final int LOAN_TYPE_CODE = 1005;//按揭员
    public static final int REDEEM_CODE = 1006;//赎楼员
    public static final int BANK_MANAGER_CODE = 1007;//银行经理
    public static final int ZONE_CODE = 1008;//地区
    public static final int Sign_Member = 1009;//签单员
    public static final int Witness = 1010;//签约见证人
    public static final int Sign_Bank = 1011;//代扣签约银行
    public static final int Company_Choose = 1012;//门店搜索

    public static final int REQUEST_CODE_REDEEM_AND_NOTARY = 600;//公证赎楼返回
    public static final int REQUEST_CODE_YELLOW = 601;//黄单
    public static final int REQUEST_CODE_REMARK = 602;//进度备注
    public static final int REQUEST_CODE_PREPAYMENT = 603;//提前还款
    public static final int REQUEST_CODE_UPDATE_SCHEDULE = 604;//更新进度
    public static final int REQUEST_CODE_BOOKS_RU_CHU = 605;//出入账
    public static final int REQUEST_CODE_BOOKS_TOTLE = 606;//转总部入账
    public static final int REQUEST_CODE_BACK = 607;//平常回退
    public static final int REQUEST_LENDING_BACK = 608;//放款回退

    public static final int REQUEST_CODE_PERMISSION_SD = 100;
    public static final int REQUEST_CODE_PERMISSION_Camera = 101;
    //    public static final int REQUEST_CODE_PERMISSION_Phone = 102;
    public static final int REQUEST_CODE_PERMISSION_MORE = 103;
    public static final int RESULT_CODE_SETTING = 104;
    public static final int RESULT_CODE_ADD_BANK = 105;//添加银行卡
    public static final int REQUEST_CODE_PERMISSION_Camera_IDCARD = 106;
    public static final int REQUEST_CODE_PERMISSION_Phone = 107;//电话权限

    public static final int POST_REFRESH_MY_TASK = 2000;//更新我的任务数据

    public static final int SHOW_BIRTH = 2001;//生日祝福

    public static final String PortraitPath = Environment.getExternalStorageDirectory() + "/CFS/cache/" + "locPortrait";//本地头像显示路径
    public static final String SerPortraitPath = Environment.getExternalStorageDirectory() + "/CFS/cache/" + "serPortrait";//服务器上图片本地路径
    public static final String DocPath = Environment.getExternalStorageDirectory() + "/CFS/cache_docs/";//服务器上图片本地路径
    public static final String AdvertisingPath = Environment.getExternalStorageDirectory() + "/CFS/cache/advertising.jpg";//广告本地路径
    public static final String QRPath = Environment.getExternalStorageDirectory() + "/CFS/cache/visitorQR.jpg";//二维码本地路径


    /**
     * 回款审核数据
     */
    public static final String BUNDLE_RETURED_AUDIT_ENTITY = "bundle_retured_audit_entity";

    /**
     * 回款审核 一级标签
     */
    public static final String BUNDLE_RETURED_AUDIT_SCHEDULE_POS = "bundle_retured_audit_schedule_pos";

    /**
     * 回款审核 二级标签
     */
    public static final String BUNDLE_RETURED_AUDIT_AUDIT_POS = "bundle_retured_audit_audit_pos";

    /**
     * 回款审核 - 从此界面进去其他界面的
     */
    public static final int REQUEST_CODE_RETURED_AUDIT = 120;


    // { -----------------------------------  网络请求相关的key -------------------------------------

    /**
     * http 请求参数 key
     */
    public static final String HTTP_PARAM_PARAM_STRING = "ParamString";
    public static final String HTTP_PARAM_USER_ID = "UserId";
    public static final String HTTP_PARAM_TRAN_NAME = "TranName";
    public static final String HTTP_PARAM_ACTION = "Action";
    public static final String HTTP_PARAM_ISUSEZIP = "IsUseZip";
    public static final String HTTP_PARAM_FUNCTION = "Function";
    public static final String HTTP_PARAM_DB_MARKER = "DBMarker";
    public static final String HTTP_PARAM_MARKER = "Marker";

    /**
     * 当前页码
     */
    public static final String HTTP_PARAM_PAGE_INDEX = "PageIndex";
    /**
     * 每页数量
     */
    public static final String HTTP_PARAM_PAGE_SIZE = "PageSize";
    /**
     * 合同号
     */
    public static final String HTTP_PARAM_CONTRACT_NO = "ContractNo";
    /**
     * 入账时间区间[开始时间] （检索条件） -- 回款审核
     */
    public static final String HTTP_PARAM_RETURED_RECORD_TIME_START = "RecordTimeStart";
    /**
     * 入账时间区间[结束时间] （检索条件） -- 回款审核
     */
    public static final String HTTP_PARAM_RETURED_RECORD_TIME_END = "RecordTimeEnd";
    /**
     * 公司iD（检索条件）
     */
    public static final String HTTP_PARAM_COMPANY_ID = "CompanyId";
    public static final String HTTP_PARAM_COMPANY_ID_ = "CompanyID";
    /**
     * 收支类型
     */
    public static final String HTTP_PARAM_PAY_TYPE = "PayType";
    /**
     * 0 未审核 1 审核通过 2审核拒绝
     */
    public static final String HTTP_PARAM_STATE = "State";
    public static final String HTTP_PARAM_BOOK_ID = "bookId";
    public static final String HTTP_PARAM_AUDIT_PASS = "AuditPass";
    public static final String HTTP_PARAM_PAY_TIME = "PayTime";
    public static final String HTTP_PARAM_COLLECT_TIME = "CollectTime";
    public static final String HTTP_PARAM_PAY_MONEY = "PayMoney";
    public static final String HTTP_PARAM_REMARK = "Remark";
    public static final String HTTP_PARAM_APP = "App";
    public static final String HTTP_PARAM_PROTOCOL_ID = "ProtocolID";
    public static final String HTTP_PARAM_BUSNES_CODE = "BusnesCode";
    public static final String HTTP_PARAM_CURRENCY = "Currency";
    public static final String HTTP_PARAM_AMOUNT = "Amount";
    public static final String HTTP_PARAM_PROTOCOL_NO = "ProtocolNo";
    public static final String HTTP_PARAM_BSNSIDS = "BsnsIds";
    public static final String HTTP_PARAM_OPERATE_ID = "OperateId";
    public static final String HTTP_PARAM_ORIGIN_CODE = "OriginCode";
    public static final String HTTP_PARAM_PARAM_BYTES = "ParamBytes";
    public static final String HTTP_PARAM_BOOK_TYPES = "BookTypes";
    // -----------------------------------  网络请求相关的key ------------------------------------- }
}

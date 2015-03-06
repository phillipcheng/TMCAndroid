
# SNAE Android Development Guide 1.0.0

##前提

OTT已在Portal上注册了商户(Tenant)。我们有了有效的TenantId(int类型)。
 
OTT用户(OTT Operator)定义了一个推广(Promotion)。我们有了有效的PromotionId(int类型)。

用户(User)已经通过OTT应用的安全认证。我们有了有效的userId(String类型)。

##红包操作接口
	import snae.tmcandroid.app.TMPublicClient;
	
	TMPublicClient publicClient = new TMPublicClient();

###列出用户已有红包
	import snae.tmcandroid.app.UserBonus;
	
	List<UserBonus> ubl = publicClient.listUserBonus(tenantId, userId);
	
###列出商户已发布的推广
	import snae.tmcandroid.app.UserPromotion;
	
	List<UserPromotion> pl = publicClient.listPromotions(tenantId);
	
###查询用户红包余额

	import snae.tmcandroid.app.UserQuota;
	
	UserQuota uq = publicClient.getQuota(tenantId, userId);
	
UserQuota有balance字段表示余额。

###用户抢红包
	import snae.tmcandroid.app.UserBonusResult;
	
	HashMap<String,Object> userProperties = new HashMap<String,Obejct>();
	
	userPropertiese.put("vip", true);
	
	UserBonusResult ubr = publicClient.grabBonus(tenantId, promotionId, userId, userProperties)
	

###用户送红包给朋友
	publicClient

###用户激活红包
	boolean ret = publicClient.activateBonus(tenantId, bonusId);

## 红包使用借口
创建TMURLManager

	TMManager tmMgr = new TMManager(); 

###用户开始会话


	boolean ret = tmMgr.start(user, tenantId);
如果ret返回false，会话创建失败。

通过getRejectReasonId, 检查原因。

	int rejectId = tmMgr.getRejectReasonId();

 
###使用红包流量
	用户程序不用修改	
    
###红包流量用完

	request会得到error status code ＝HTTP_UNAUTHORIZED
	
	表示红包流量用完

###用户停止会话
	tmMgr.end();



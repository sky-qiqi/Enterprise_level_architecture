import request, { post, get } from "@/utils/request";
// 登陆
export const login = (params) => post("/login",params)
//登出
export const logout = () => get("/login/logout")
//获取登陆用户信息
export const getUser = () => get("/user/getUserInfo")
//修改个人信息
export const setUserInfo = (params) => post("/user/setUserInfo",params)
//修改个人头像
export const setUserAvatar = (params) => get("/user/setUserAvatar",params)
// 新增两个API接口
// 获取个性化推荐
export const getRecommendRoutes = () => get("/favor/recommend")
// 获取热度排行榜
export const getRankRoutes = () => get("/favor/recommend/rank")
export const changePassword = (params) => post("/user/changePassword",params)
//保存用户
export const saveUser = (params) => post("/user/saveUser",params)
//获取邮箱验证码
export const getEmailReg = (params) => get("/user/getEmailReg",params)
//忘记密码
export const forgetPassword = (params) => post("/user/forgetPassword",params)
//获取轮播图
export const getRotationsList = (params) => get("/rotations/getRotationsList",params)
//获取首页景点
export const getAttractionsIndex = (params) => get("/attractions/getAttractionsIndex",params)
//获取用户数量
export const getUserCount = (params) => get("/user/getUserCount",params)
//获取首页路线
export const getLineIndex = (params) => get("/line/getLineIndex",params)
//查询景点
export const getAttractionsPage = (params) => post("/attractions/getAttractionsPage",params)
//根据id查询景点
export const getAttractionsById = (params) => get("/attractions/getAttractionsById",params)
//查询旅游线路
export const getLinePage = (params) => post("/line/getLinePage",params)
//根据id查询旅游线路
export const getLineById = (params) => get("/line/getLineById",params)
//查询酒店
export const getHotelPage = (params) => post("/hotel/getHotelPage",params)
//根据id查询酒店
export const getHotelById = (params) => get("/hotel/getHotelById",params)
//后去列表
export const getAttractionsList = (params) => get("/attractions/getAttractionsList",params)
//查询资讯
export const getForumPage = (params) => post("/forum/getForumPage",params)
//根据id查询资讯
export const getForumById = (params) => get("/forum/getForumById",params)
//查询收藏
export const getFavorPage = (params) => post("/favor/getFavorPage",params)
//根据id查询收藏
export const getFavorById = (params) => get("/favor/getFavorById",params)
//保存收藏
export const saveFavor = (params) => post("/favor/saveFavor",params)
//更新收藏
export const editFavor = (params) => post("/favor/editFavor",params)
//删除Favor
export const removeFavor = (params) => get("/favor/removeFavor",params)
//获取收藏
export const getFavor = (params) => get("/favor/getFavor",params)
//查询景点预约
export const getAttractionOrderPage = (params) => post("/order/getAttractionOrderPage",params)
//保存景点预约
export const saveAttractionOrder = (params) => post("/order/saveAttractionOrder",params)
//更新景点预约
export const editAttractionOrder = (params) => post("/order/editAttractionOrder",params)
//获取房型
export const getHotelItemList = (params) => get("/item/getHotelItemList",params)
//查询酒店预约
export const getHotelOrderPage = (params) => post("/order/getHotelOrderPage",params)
//根据id查询酒店预约
export const getHotelOrderById = (params) => get("/order/getHotelOrderById",params)
//保存酒店预约
export const saveHotelOrder = (params) => post("/order/saveHotelOrder",params)
//更新酒店预约
export const editHotelOrder = (params) => post("/order/editHotelOrder",params)
//查询评论
export const getCommentsPage = (params) => post("/comments/getCommentsPage",params)
//根据id查询评论
export const getCommentsById = (params) => get("/comments/getCommentsById",params)
//保存评论
export const saveComments = (params) => post("/comments/saveComments",params)
//更新评论
export const editComments = (params) => post("/comments/editComments",params)
//删除Comments
export const removeComments = (params) => get("/comments/removeComments",params)

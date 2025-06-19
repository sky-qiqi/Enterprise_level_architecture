import request, { post, get } from "@/utils/request";

// export function products(params) {
//     return request({
//       url: "/api/v1/products",  //接口路径
//       method: "get",  //接口方法
//       headers: { 'Content-Type': 'multipart/form-data' }, //给接口添加请求头
//       params  //接口参数
//     });
// }

//-------------------------------登录---------------------------------------
// 登录
export const login = (params) => post("/login",params)
//登出
export const logout = () => get("/login/logout")

//-------------------------------用户---------------------------------------
// 查询用户
export const getUserPage = (params) => post("/user/getUserPage",params)
//根据id查询用户
export const getUserById = (params) => get("/user/getUserById",params)
//保存用户
export const saveUser = (params) => post("/user/saveUser",params)
//更新用户
export const editUser = (params) => post("/user/editUser",params)
//删除用户
export const removeUser = (params) => get("/user/removeUser",params)
//重置密码
export const resetPassword = (params) => post("/user/resetPassword",params)
//获取登录用户信息
export const getUser = () => get("/user/getUserInfo")
//修改个人信息
export const setUserInfo = (params) => post("/user/setUserInfo",params)
//修改个人头像
export const setUserAvatar = (params) => get("/user/setUserAvatar",params)
//修改密码
export const changePassword = (params) => post("/user/changePassword",params)

//-------------------------------景点---------------------------------------
//查询景点
export const getAttractionsPage = (params) => post("/attractions/getAttractionsPage",params)
//根据id查询景点
export const getAttractionsById = (params) => get("/attractions/getAttractionsById",params)
//保存景点
export const saveAttractions = (params) => post("/attractions/saveAttractions",params)
//更新景点
export const editAttractions = (params) => post("/attractions/editAttractions",params)
//删除景点
export const removeAttractions = (params) => get("/attractions/removeAttractions",params)
//后去列表
export const getAttractionsList = (params) => get("/attractions/getAttractionsList",params)

//-------------------------------旅游线路---------------------------------------
//查询旅游线路
export const getLinePage = (params) => post("/line/getLinePage",params)
//根据id查询旅游线路
export const getLineById = (params) => get("/line/getLineById",params)
//保存旅游线路
export const saveLine = (params) => post("/line/saveLine",params)
//更新旅游线路
export const editLine = (params) => post("/line/editLine",params)
//删除SysLine
export const removeLine = (params) => get("/line/removeLine",params)

//-------------------------------酒店---------------------------------------
//查询酒店
export const getHotelPage = (params) => post("/hotel/getHotelPage",params)
//根据id查询酒店
export const getHotelById = (params) => get("/hotel/getHotelById",params)
//保存酒店
export const saveHotel = (params) => post("/hotel/saveHotel",params)
//更新酒店
export const editHotel = (params) => post("/hotel/editHotel",params)
//删除SysHotel
export const removeHotel = (params) => get("/hotel/removeHotel",params)

//-------------------------------房型---------------------------------------
//查询房型
export const getHotelItemPage = (params) => post("/item/getHotelItemPage",params)
//根据id查询房型
export const getHotelItemById = (params) => get("/item/getHotelItemById",params)
//保存房型
export const saveHotelItem = (params) => post("/item/saveHotelItem",params)
//更新房型
export const editHotelItem = (params) => post("/item/editHotelItem",params)
//删除SysHotelItem
export const removeHotelItem = (params) => get("/item/removeHotelItem",params)

//-------------------------------资讯---------------------------------------
//查询资讯
export const getForumPage = (params) => post("/forum/getForumPage",params)
//根据id查询资讯
export const getForumById = (params) => get("/forum/getForumById",params)
//保存资讯
export const saveForum = (params) => post("/forum/saveForum",params)
//更新资讯
export const editForum = (params) => post("/forum/editForum",params)
//删除SysForum
export const removeForum = (params) => get("/forum/removeForum",params)

//-------------------------------评论---------------------------------------
//查询评论
export const getCommentsPage = (params) => post("/comments/getCommentsPage",params)
//根据id查询评论
export const getCommentsById = (params) => get("/comments/getCommentsById",params)
//保存评论
export const saveComments = (params) => post("/comments/saveComments",params)
//更新评论
export const editComments = (params) => post("/comments/editComments",params)
//删除SysComments
export const removeComments = (params) => get("/comments/removeComments",params)

//-------------------------------景点预约---------------------------------------
//查询景点预约
export const getAttractionOrderPage = (params) => post("/order/getAttractionOrderPage",params)
//根据id查询景点预约
export const getAttractionOrderById = (params) => get("/order/getAttractionOrderById",params)
//保存景点预约
export const saveAttractionOrder = (params) => post("/order/saveAttractionOrder",params)
//更新景点预约
export const editAttractionOrder = (params) => post("/order/editAttractionOrder",params)
//删除SysAttractionOrder
export const removeAttractionOrder = (params) => get("/order/removeAttractionOrder",params)

//-------------------------------酒店预约---------------------------------------
//查询酒店预约
export const getHotelOrderPage = (params) => post("/order/getHotelOrderPage",params)
//根据id查询酒店预约
export const getHotelOrderById = (params) => get("/order/getHotelOrderById",params)
//保存酒店预约
export const saveHotelOrder = (params) => post("/order/saveHotelOrder",params)
//更新酒店预约
export const editHotelOrder = (params) => post("/order/editHotelOrder",params)
//删除SysHotelOrder
export const removeHotelOrder = (params) => get("/order/removeHotelOrder",params)

//-------------------------------轮播图---------------------------------------
//查询轮播图
export const getRotationsPage = (params) => post("/rotations/getRotationsPage",params)
//根据id查询轮播图
export const getRotationsById = (params) => get("/rotations/getRotationsById",params)
//保存轮播图
export const saveRotations = (params) => post("/rotations/saveRotations",params)
//更新轮播图
export const editRotations = (params) => post("/rotations/editRotations",params)
//删除SysRotations
export const removeRotations = (params) => get("/rotations/removeRotations",params)

//图表
export const getManageData = (params) => get("/login/getManageData",params)


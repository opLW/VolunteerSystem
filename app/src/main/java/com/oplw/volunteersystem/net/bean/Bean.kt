package com.oplw.volunteersystem.net.bean

import java.util.*

/**
 *
 *   @author opLW
 *   @date  2019/7/10
 */

/**
 * 所有返回结果的一个模板
 */
class GeneralResult<T>(
    var code: Int,
    var msg: String,
    var data: T?,
    var count: Int
)

/**
 * @param admin 0代表的是普通用户，1代表的是管理员
 */
data class User(
    var id: Int,
    var email: String,
    var username: String,
    var password: String = "",
    var admin: Int = 0,
    var createdAt: Long  = 0L
)

/**
 * 存放一般文章的bean
 * @param poster 该文章的配图对应的id，用于进行字节流访问
 * @param textId 该文章的富文本id，用于点击获取详细的文章
 */
data class Article(
    var id: Int,
    var title: String,
    var poster: Int,
    var channelId: Int,
    var textId: Int,
    var createdAt: Long
)

/**
 * 获取一级的栏目
 * @param irChannels 存放二级栏目
 */
data class TopColumn(
    var id: Int,
    var name: String,
    var irChannels: ArrayList<SecondaryColumn>?
)

/**
 * 存放二级栏目
 */
data class SecondaryColumn(
    var id: Int,
    var name: String,
    var level: Int,
    var createdAt: Long
)

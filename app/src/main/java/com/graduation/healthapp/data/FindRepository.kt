package com.graduation.healthapp.data

class FindRepository(private val datasource: FindDataSource) {

    //这里做缓存处理
    fun getAllSmalClass(block: (result: Result) -> Unit) {
        datasource.getAllSmalClass {
            block.invoke(it)
            if (it is Result.Success) {

            } else {

            }
        }
    }

    fun getPageData(page: Int, limit: Int, user: String,block: (result: Result) -> Unit) {
        datasource.getPageData(page,limit,user) {
            block.invoke(it)
        }
    }

    fun getPageData2(page: Int, limit: Int, user: String,typeid:Int,block: (result: Result) -> Unit) {
        datasource.getPageData2(page,limit,user,typeid) {
            block.invoke(it)
        }
    }

    fun favour(id: String, user: String,block: (result: Result) -> Unit) {
        datasource.favour(id,user) {
            block.invoke(it)
        }
    }

    fun collect(id: String, user: String,block: (result: Result) -> Unit) {
        datasource.collect(id,user) {
            block.invoke(it)
        }
    }

}
//package com.primedia.primedia_sample_app.room.repositories
//
//import com.primedia.primedia_sample_app.App
//import com.primedia.primedia_sample_app.rest.BaseRepository
//import com.primedia.primedia_sample_app.rest.RetrofitService
//import com.primedia.primedia_sample_app.room.AppDatabase
//import org.kodein.di.generic.instance
//
//class SearchRepository(private val service: RetrofitService): BaseRepository() {
//
//    private val appDatabase: AppDatabase by App.kodein.instance()
//    private val searchHistoryDao = appDatabase.searchHistoryDao()
//
//    fun getSearchResults(searchTerm: String, pageNumber: Int, successFunction: (success: Boolean, stationShow: List<StreamItemDataModel>?) -> Unit) {
//        return service.getSearchResults(searchTerm, pageNumber, successFunction)
//    }
//
//    fun insertSearch(searchEntity: SearchHistoryEntity){
//        ioThread {
//            searchHistoryDao.insertSearch(searchEntity)
//        }
//    }
//
//    fun getRecentSearches(successFunction: ((searchHistory: List<SearchHistoryEntity>?) -> Unit)) {
//        ioThread {
//            val history = searchHistoryDao.getRecentSearches()
//            if(!history.isNullOrEmpty()) {
//                successFunction(history)
//            }else{
//                successFunction(listOf())
//            }
//        }
//    }
//
//    fun deleteRecentSearchItem(identifier: String){
//        ioThread {
//            searchHistoryDao.deleteRecentSearchItem(identifier)
//        }
//    }
//
//    fun deleteRecentSearchHistory(){
//        ioThread {
//            searchHistoryDao.clearAllSearches()
//        }
//    }
//
//    fun deleteOldSearchHistoryItems(){
//        ioThread {
//            searchHistoryDao.removeOldSearchHistoryItems()
//        }
//    }
//
//    suspend fun getTrendingSearches(): ApiResponse<List<StreamItemDataModel>>? {
//        return performCall { service.client.getTrendingSearches() }
//    }
//}
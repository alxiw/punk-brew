package io.github.alxiw.punkbrew.data.source

import io.github.alxiw.punkbrew.data.api.BeerResponse
import io.github.alxiw.punkbrew.data.api.PunkService
import io.github.alxiw.punkbrew.data.db.BeerEntity
import io.github.alxiw.punkbrew.data.map.BeerMapper.fromResponse
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.disposables.Disposable
import io.reactivex.schedulers.Schedulers

class PunkRemoteSource(private val service: PunkService) {

    private val disposables = ArrayList<Disposable>()

    fun searchBeers(
        query: String?,
        page: Int,
        perPage: Int,
        onSuccess: (beers: List<BeerEntity>) -> Unit,
        onError: (error: String) -> Unit
    ) {
        val number = query?.toIntOrNull()
        val single =
            if (number != null && number > 0) {
                service.getBeersById(page, perPage, number)
            } else {
                service.getBeers(page, perPage, query)
            }
        disposables.add(
            single.subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(
                    { response: List<BeerResponse> ->
                        onSuccess(fromResponse(response))
                    },
                    { e: Throwable ->
                        onError(e.message ?: "Unknown error")
                    }
                )
        )
    }
}

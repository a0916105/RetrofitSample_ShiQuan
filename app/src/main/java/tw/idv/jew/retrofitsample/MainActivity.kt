package tw.idv.jew.retrofitsample

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import io.reactivex.rxjava3.android.schedulers.AndroidSchedulers
import io.reactivex.rxjava3.core.Scheduler
import io.reactivex.rxjava3.schedulers.Schedulers
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.Retrofit
import retrofit2.adapter.rxjava3.RxJava3CallAdapterFactory
import retrofit2.converter.gson.GsonConverterFactory
import java.lang.Exception

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        val adapter = RepoAdapter()
        val recyclerView = findViewById<RecyclerView>(R.id.recyclerView)
        recyclerView.adapter = adapter
        recyclerView.layoutManager = LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false)

        val retrofit = Retrofit.Builder()
                .baseUrl("https://api.github.com")
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build()
        val service = retrofit.create(GitHubService::class.java)

        //Sync way：execute()不適合在UI執行緒中，使用
        /*try {
            val response = service.listRepos("a0916105").execute()
            if (response.isSuccessful) {
                // success
            }else {
                //application level fail
            }
        }catch (exception: Exception) {
            //network fail or other error
        }*/

        //Async way：enqueue()
        /*service.listRepos("a0916105")
            .enqueue(object : Callback<List<Repo>> {
                override fun onFailure(call: Call<List<Repo>>, t: Throwable) {
                    //network fail
                    Toast.makeText(this@MainActivity, "Network fail or other error", Toast.LENGTH_SHORT).show()
                }

                override fun onResponse(call: Call<List<Repo>>, response: Response<List<Repo>>) {
                    if (response.isSuccessful) {
                        // success
                    } else {
                        //application level fail
                        Toast.makeText(this@MainActivity, "Application level fail", Toast.LENGTH_SHORT).show()
                    }
                }
            })*/

        //RxJava Response
        /*service.listRepos("a0916105")
            .subscribeOn(Schedulers.io())
            .observeOn(AndroidSchedulers.mainThread())
            .subscribe({
                //success
            },{
                //network fail
            })*/
            /*.subscribe(object : SingleObserver<Response<List<Repo>>> {
                override fun onSuccess(t: Response<List<Repo>>) {
                    //use data here
                }

                override fun onSubscribe(d: Disposable) {
                }

                override fun onError(e: Throwable) {
                }

            })*/

        //Coroutine Response
        lifecycleScope.launch {
            try {
                val result = service.listRepos("a0916105")
                adapter.submitList(result)
            }catch (e: Exception){
                //network fail
            }
        }
    }
}
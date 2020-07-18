package com.shliama.augmentedvideotutorial

import android.app.ActivityManager
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import com.google.gson.Gson
import com.shliama.augmentedvideotutorial.DataHandling.Dataholder
import com.shliama.augmentedvideotutorial.DataHandling.ItemSelectedInterface
import com.shliama.augmentedvideotutorial.Network.AssetApiResponse
import com.shliama.augmentedvideotutorial.Network.MagazineApiResponse
import com.shliama.augmentedvideotutorial.Network.ServiceGenerator
import com.shliama.augmentedvideotutorial.Network.apiInterfaces
import com.squareup.picasso.Picasso
import com.squareup.picasso.Target
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ArActivity : AppCompatActivity() {

    private val openGlVersion by lazy {
        (getSystemService(Context.ACTIVITY_SERVICE) as ActivityManager)
            .deviceConfigurationInfo
            .glEsVersion
    }

    private lateinit var dialog: AlertDialog

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        if (intent != null && intent.hasExtra("base_url"))
            Dataholder.baseUrl = intent.getStringExtra("base_url")
        else Dataholder.baseUrl = Dataholder.testUrl

        if (Dataholder.magazineVersion == null) {
            if (Dataholder.processDone) {
                checkArAccess();
            } else {
                checkMagazineVersion()
            }
        } else {
            checkVersionPhotos(
                Dataholder.magazineVersion.get(
                    Dataholder.magazineVersion.size - 1).version);
        }
    }

    private fun checkArAccess() {
        if (openGlVersion.toDouble() >= MIN_OPEN_GL_VERSION) {
            supportFragmentManager.inTransaction {
                add(
                    R.id.fragmentContainer,
                    ArVideoFragment()
                )
            }
        } else {
            AlertDialog.Builder(this)
                .setTitle("Device is not supported")
                .setMessage("OpenGL ES 3.0 or higher is required. The device is running OpenGL ES $openGlVersion.")
                .setPositiveButton(android.R.string.ok) { _, _ -> finish() }
                .show()
        }
    }

    private fun checkVersionPhotos(i: kotlin.Int) {
        val call: Call<ResponseBody> = ServiceGenerator.createService(
            apiInterfaces::class.java)
            .getAsset(i)
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                ApiFailed()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                var res: AssetApiResponse =
                    Gson().fromJson(response.body()?.string(), AssetApiResponse::class.java)
                savePhotos(res)
            }
        })
    }

    private fun savePhotos(res: AssetApiResponse) {
        Dataholder.photos.clear()
        for (item in res.result.indices) {
            getPreferences(Context.MODE_PRIVATE).edit().putString(
                "magazine" + Dataholder.magazineVersion[Dataholder.magazineVersion.size-1].version + "_" + item,
                res.result.get(item).inputImageUrl
            ).apply()
            getPreferences(Context.MODE_PRIVATE).edit()
                .putString(res.result.get(item).inputImageUrl, res.result.get(item).outPutFileUrl)
                .apply()
            var pair: android.util.Pair<String, String> = android.util.Pair(
                res.result.get(item).inputImageUrl,
                res.result.get(item).outPutFileUrl
            )
            Dataholder.photos.add(pair)

            val mTarget: Target = object : Target {
                override fun onBitmapLoaded(bitmap: Bitmap?, loadedFrom: Picasso.LoadedFrom?) {
                    Log.e("tag", "onBitmapLoaded: " + bitmap )
                    Dataholder.photosBitmaps.add(bitmap)
                    if(Dataholder.photosBitmaps.size == Dataholder.photos.size) {
                        Dataholder.processDone = true
                        checkArAccess()
                    }
                }

                override fun onPrepareLoad(drawable: Drawable?) {
                }

                override fun onBitmapFailed(errorDrawable: Drawable?) {
                }
            }
            for (item in Dataholder.photos.indices) {
                    Picasso.with(this)
                        .load(Dataholder.photos[item].first as String)
                        .into(mTarget)
            }

        }
        Dataholder.processDone = true
    }

    private fun checkMagazineVersion() {
        val call: Call<ResponseBody> =
            ServiceGenerator.createService(
                apiInterfaces::class.java).latestVersion
        call.enqueue(object : Callback<ResponseBody> {
            override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                ApiFailed()
            }

            override fun onResponse(call: Call<ResponseBody>, response: Response<ResponseBody>) {
                var res: MagazineApiResponse =
                    Gson().fromJson(response.body()?.string(), MagazineApiResponse::class.java)
                Dataholder.magazineVersion = res.result
                var dialog = SelectorDialog(this@ArActivity, res.result, object:
                    ItemSelectedInterface {
                    override fun itemSelected(i: Int) {
                        checkVersionPhotos(i)
                    }
                } )
                dialog.show()
            }

        })
    }

    private fun ApiFailed() {
        Toast.makeText(this, "خطا در برقراری ارتباط با سرور", Toast.LENGTH_SHORT).show()
        finish()
    }

    private inline fun FragmentManager.inTransaction(func: FragmentTransaction.() -> FragmentTransaction) {
        beginTransaction().func().commit()
    }

    companion object {
        private const val MIN_OPEN_GL_VERSION = 3.0
    }

    override fun onResume() {
        super.onResume()
        if (!isPackageInstalled("com.google.ar.core", packageManager)) {
            installGooglePlayServicesForAR()
        }
    }

    private fun isPackageInstalled(
        packageName: String,
        packageManager: PackageManager
    ): Boolean {
        return try {
            packageManager.getPackageInfo(packageName, 0)
            true
        } catch (e: PackageManager.NameNotFoundException) {
            false
        }
    }

    private fun installGooglePlayServicesForAR() {
        val builder =
            AlertDialog.Builder(this, R.style.myAlert)
        dialog = builder.setCancelable(false)
            .setIcon(resources.getDrawable(android.R.drawable.ic_dialog_alert))
            .setPositiveButton(
                "نصب از فروشگاه"
            ) { dialogInterface, i ->
                val goToMarket = Intent(Intent.ACTION_VIEW)
                    .setData(Uri.parse("https://play.google.com/store/apps/details?id=com.google.ar.core"))
                Toast.makeText(
                    this@ArActivity,
                    "Installing Google Play Services for AR",
                    Toast.LENGTH_LONG
                )
                    .show()
                startActivity(goToMarket)
            }.setNegativeButton(
                "فعلا نه"
            ) { dialogInterface, i ->
                Toast.makeText(
                    this@ArActivity,
                    "بدون هسته ی واقعیت افزوده، اجرای این بخش مقدور نمیباشد",
                    Toast.LENGTH_SHORT
                ).show()
                finish()
            }.setTitle("نیازمند هسته اجرای واقعیت افزوده است")
            .setMessage("لطفا برای نصب هسته ی اجرای واقعیت افزوده به فروشگاه گوگل مراجعه کنید")
            .create()
        dialog.show()
    }
}
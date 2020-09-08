package com.travels.searchtravels.activity

import android.Manifest
import android.accounts.Account
import android.accounts.AccountManager
import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.View
import android.view.animation.AccelerateDecelerateInterpolator
import android.widget.Toast
import androidx.core.app.ActivityCompat
import com.dynamitechetan.flowinggradient.FlowingGradientClass
import com.google.android.gms.auth.GoogleAuthUtil
import com.google.android.gms.common.AccountPicker
import com.google.api.services.vision.v1.model.*
import com.preview.planner.prefs.AppPreferences
import com.travels.searchtravels.R
import com.travels.searchtravels.api.GeolocationApi
import com.travels.searchtravels.api.OnVisionApiListener
import com.travels.searchtravels.api.VisionApi
import com.travels.searchtravels.utils.*
import com.travels.searchtravels.utils.ImageHelper.*
import io.reactivex.android.schedulers.AndroidSchedulers
import io.reactivex.schedulers.Schedulers
import kotlinx.android.synthetic.main.activity_main.*
import org.json.JSONArray
import org.json.JSONObject
import java.io.BufferedReader
import java.io.IOException
import java.io.InputStreamReader
import java.net.HttpURLConnection
import java.net.URL

class MainActivity : AppCompatActivity() {
    private var accessToken: String? = null
    internal val REQUEST_GALLERY_IMAGE = 10
    internal val REQUEST_CODE_PICK_ACCOUNT = 11
    internal val REQUEST_ACCOUNT_AUTHORIZATION = 12
    internal val REQUEST_PERMISSIONS = 13
    private val LOG_TAG = "MainActivity"
    internal var mAccount: Account? = null
    private val geoApi = GeolocationApi.create()
    var askedPermission = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        startStarting()
//        if (AppPreferences.isFirstLaunch(applicationContext)){
//          startStarting()
//        } else {
//            startMain()
//        }


        //showLoader()
        pickImageBTN.setOnClickListener {
            if (AppPreferences.isFirstLaunch(applicationContext)){
                 Toast.makeText(applicationContext, "Доступ Contacts необходим для работы Google Vision API", Toast.LENGTH_LONG).show()
             }
            AppPreferences.setFirstLaunch(applicationContext)
            startPicking()
        }
    }

    private fun startStarting(){
        mainRL.visibility = View.GONE
        startingRL.visibility = View.VISIBLE
        val grad = FlowingGradientClass()
        grad.setBackgroundResource(R.drawable.horizontal_loader_button)
            .onRelativeLayout(nextBTN)
            .setTransitionDuration(3000)
            .start()
        nextBTN.setOnClickListener{
            startMain()
        }
        startingRL.animate().alpha(1f).setInterpolator(AccelerateDecelerateInterpolator()).setDuration(2000);

    }
    private fun startMain(){
        mainRL.visibility = View.VISIBLE
        startingRL.visibility = View.GONE
        val grad = FlowingGradientClass()
        grad.setBackgroundResource(R.drawable.loader_button_background)
            .onRelativeLayout(strokeRL)
            .setTransitionDuration(3000)
            .start()
        grad.setBackgroundResource(R.drawable.loader_button_background)
            .onRelativeLayout(buttonRL)
            .setTransitionDuration(3000)
            .start()

        AnimationHelper.animateBtn(pickPhotoRL, applicationContext, AnimationFinished {
            AnimationHelper.animateBtnLoop(strokeRL, applicationContext, 200, 220)
            AnimationHelper.animateBtnLoop(blackHole, applicationContext, 195, 215)
            AnimationHelper.animateBtnLoop(buttonRL, applicationContext, 170, 190)
        })
        contentLL.animate().alpha(1f).setInterpolator(AccelerateDecelerateInterpolator()).setDuration(1000);

        closeRL.setOnClickListener{
            errorRL.visibility = View.GONE
        }
        loadAnotherTV.setOnClickListener{
            startPicking()
        }

    }

    private fun startPicking(){
        errorRL.visibility = View.GONE
        if (!askedPermission) {
            ActivityCompat.requestPermissions(
                this,
                arrayOf(Manifest.permission.GET_ACCOUNTS),
                REQUEST_PERMISSIONS
            )
        } else {
            launchImagePicker()
        }
    }
    private fun launchImagePicker() {
        val intent = Intent()
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT
        startActivityForResult(
            Intent.createChooser(intent, "Select an image"),
            REQUEST_GALLERY_IMAGE
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        when (requestCode) {
            REQUEST_PERMISSIONS -> if (grantResults.size > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                getAuthToken()
                askedPermission = true
            } else {
//                askedPermission = false
//                startPicking()
                Toast.makeText(this, "Permission необходим для работы Google Vision API, разрешите его в настройках", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_GALLERY_IMAGE && resultCode == Activity.RESULT_OK && data != null) {
            uploadImage(data.data)
        } else if (requestCode == REQUEST_CODE_PICK_ACCOUNT) {
            if (resultCode == Activity.RESULT_OK) {
                val email = data!!.getStringExtra(AccountManager.KEY_ACCOUNT_NAME)
                val am = AccountManager.get(this)
                val accounts = am.getAccountsByType(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)
                for (account in accounts) {
                    if (account.name == email) {
                        mAccount = account
                        break
                    }
                }
                getAuthToken()
            } else if (resultCode == Activity.RESULT_CANCELED) {
//                askedPermission = false
//                startPicking()
                Toast.makeText(this, "No Account Selected", Toast.LENGTH_SHORT)
                    .show()
            }
        } else if (requestCode == REQUEST_ACCOUNT_AUTHORIZATION) {
            if (resultCode == Activity.RESULT_OK) {
                val extra = data!!.extras
                onTokenReceived(extra!!.getString("authtoken"))
            } else if (resultCode == Activity.RESULT_CANCELED) {
                askedPermission = false
                startPicking()
                Toast.makeText(this, "Авторизацию необходима для работы Google Vision API", Toast.LENGTH_SHORT)
                    .show()
            }
        }
    }

    private fun showLoader(){
        progressRL.visibility = View.VISIBLE
        AnimationHelper.roundAnimation(loaderIV, 360f)
        pickPhotoRL.visibility = View.GONE
    }
    private fun hideLoader(){
        progressRL.visibility = View.GONE
        pickPhotoRL.visibility = View.VISIBLE
    }

    fun uploadImage(uri: Uri?) {
        if (uri != null) {
            showLoader()
            try {
                val bitmap = resizeBitmap(
                    MediaStore.Images.Media.getBitmap(contentResolver, uri)
                )
                Constants.PICKED_BITMAP = bitmap
                VisionApi.findLocation(
                    bitmap,
                    AppPreferences.getToken(applicationContext),
                    object : OnVisionApiListener {

                        override fun onSuccess(latLng: LatLng) {
                            getCity(latLng, "en-GB")
                            val handler = Handler()
                            handler.postDelayed({
                                getCity(latLng, "ru")
                            },200)

                            L.d("onSuccess = " + latLng)

                        }

                        override fun onErrorPlace(category: String) {
                            loadByCategory(category)

                        }

                        override fun onError() {
                            hideLoader()
                            errorRL.visibility = View.VISIBLE
                            L.d("onError")

                        }
                    })
            } catch (e: IOException) {
                Log.e(LOG_TAG, e.message)
            }

        } else {
            Log.e(LOG_TAG, "Null image was returned.")
        }
    }

    private fun loadByCategory(category: String) {
        var city = "Rimini"
        var cityRu = "Римини"
        when(category){
            "sea" -> {
                city = "Rimini"
                cityRu = "Римини"
            }
            "ocean" -> {
                city = "Rimini"
                cityRu = "Римини"
            }
            "beach" -> {
                city = "Rimini"
                cityRu = "Римини"
            }
            "mountain" -> {
                city = "Sochi"
                cityRu = "Сочи"
            }
            "snow" -> {
                city = "Helsinki"
                cityRu = "Хельсинки"
            }
            else -> {
                city = "Rimini"
                cityRu = "Римини"
            }
        }
        Constants.PICKED_CITY_EN = city
        Constants.PICKED_CITY_RU = cityRu
        hideLoader()
        startDetailsActivity()
    }

    private fun getCity(latLng: LatLng, language : String) {

        val latLngString = latLng.latitude.toString() + "," + latLng.longitude.toString()
        geoApi.getCity(latLngString, language)
            .observeOn(AndroidSchedulers.mainThread())
            .subscribeOn(Schedulers.io())
            .subscribe({ result ->

                var city = ""
                val resultsArray = result.asJsonObject.getAsJsonArray("results")
                if (resultsArray != null) {
                    val size = resultsArray.size()

                    if (size > 0) {

                        city = when {
                            size > 3 -> resultsArray[size - 3].asJsonObject.get("formatted_address").asString
                            size > 2 -> resultsArray[size - 2].asJsonObject.get("formatted_address").asString
                            size > 1 -> resultsArray[size - 1].asJsonObject.get("formatted_address").asString
                            else -> resultsArray[0].asJsonObject.get("formatted_address").asString
                        }
                    }
                }
                if (language == "en-GB") {
                    if (city.contains(",")) {
                        val array = city.split(",")
                        if (array.count() > 2) {
                            Constants.PICKED_CITY_EN = array[1]
                        } else {
                            Constants.PICKED_CITY_EN = array[0]
                        }
                    } else {
                        Constants.PICKED_CITY_EN = city
                    }
                } else {
                    if (city.contains(",")) {
                        val array = city.split(",")
                        if (array.count() > 2) {
                            Constants.PICKED_CITY_RU = array[1]
                        } else {
                            Constants.PICKED_CITY_RU = array[0]
                        }
                    } else {
                        Constants.PICKED_CITY_RU = city
                    }
                    hideLoader()
                    startDetailsActivity()
                }
                if (Constants.PICKED_CITY_EN.trim() == "Praha"){
                    Constants.PICKED_CITY_EN = "Prague"
                }
                if (Constants.PICKED_CITY_EN.trim() == "Barbados"){
                    Constants.PICKED_CITY_EN = "Bridgetown"
                }
                if (Constants.PICKED_CITY_EN.trim() == "Kyiv"){
                    Constants.PICKED_CITY_EN = "Kiev"
                }
                if (Constants.PICKED_CITY_EN.contains("Venice")){
                    Constants.PICKED_CITY_EN = "Venice"
                }
                L.d("city ru= " + Constants.PICKED_CITY_RU)
                L.d("city en= " + Constants.PICKED_CITY_EN)
                L.d("city = " + city)
            }, { error ->
                error.printStackTrace()
            })
    }


    private fun startDetailsActivity() {
        val intent = Intent(this, DetailsActivity::class.java)
        startActivity(intent)
    }


    private fun pickUserAccount() {
        val accountTypes = arrayOf(GoogleAuthUtil.GOOGLE_ACCOUNT_TYPE)
        val intent = AccountPicker.newChooseAccountIntent(
            null, null,
            accountTypes, false, null, null, null, null
        )
        startActivityForResult(intent, REQUEST_CODE_PICK_ACCOUNT)
    }

    private fun getAuthToken() {
        val SCOPE = "oauth2:https://www.googleapis.com/auth/cloud-platform"
        if (mAccount == null) {
            pickUserAccount()
        } else {
            GetTokenTask(this, mAccount, SCOPE, REQUEST_ACCOUNT_AUTHORIZATION)
                .execute()
        }
    }

    fun onTokenReceived(token: String?) {
        if (token != null) {
            AppPreferences.setToken(applicationContext, token)
            accessToken = token
            launchImagePicker()
        }
    }


}

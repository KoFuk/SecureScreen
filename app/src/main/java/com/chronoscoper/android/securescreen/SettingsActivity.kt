/*
 * Copyright 2017 KoFuk
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.chronoscoper.android.securescreen

import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ActivityNotFoundException
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.preference.PreferenceManager
import android.support.v4.app.NotificationCompat
import android.support.v4.content.ContextCompat
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.widget.Switch
import android.widget.TextView
import com.chronoscoper.library.licenseviewer.LicenseViewer
import kotterknife.bindView

class SettingsActivity : AppCompatActivity() {
    companion object {
        private const val DEVELOPER_WEB_ADDRESS = "http://www.chronoscoper.com/"
    }

    private val startButton by bindView<View>(R.id.start)
    private val mVersionLabel by bindView<TextView>(R.id.version)
    private val mDeveloperLabel by bindView<View>(R.id.developer)
    private val mStartOnBootSwitch by bindView<Switch>(R.id.start_on_boot)
    private val mOssLicenseLabel by bindView<View>(R.id.oss_license)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_settings)

        setupStartOnBootSwitch()
        setupAppInfo()

        startButton.setOnClickListener {
            val notification = NotificationCompat.Builder(applicationContext)
                    .setSmallIcon(R.drawable.ic_notification)
                    .setContentTitle(getString(R.string.notification_title))
                    .setContentText(getString(
                            R.string.notification_message))
                    .setPriority(NotificationCompat.PRIORITY_MAX)
                    .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
                    .setColor(ContextCompat.getColor(
                            this, R.color.colorPrimary))
            val pendingIntent = PendingIntent.getActivity(applicationContext, 1,
                    Intent(applicationContext, SecureActivity::class.java), 0)
            notification.setContentIntent(pendingIntent)
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(1, notification.build())
        }
    }

    private fun setupStartOnBootSwitch() {
        val preferences = PreferenceManager.getDefaultSharedPreferences(this)
        mStartOnBootSwitch.isChecked = preferences.getBoolean("start_on_boot", false)
        mStartOnBootSwitch.setOnCheckedChangeListener { _, isChecked ->
            preferences.edit().putBoolean("start_on_boot", isChecked).apply()
        }
    }

    private fun setupAppInfo() {
        mVersionLabel.text = BuildConfig.VERSION_NAME
        mDeveloperLabel.setOnClickListener {
            try {
                startActivity(Intent(Intent.ACTION_VIEW,
                        Uri.parse(DEVELOPER_WEB_ADDRESS)))
            } catch (ignore: ActivityNotFoundException) {
            }
        }
        mOssLicenseLabel.setOnClickListener {
            LicenseViewer.open(this, getString(R.string.oss_license), true)
        }
    }
}
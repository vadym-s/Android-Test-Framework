/*
 * Copyright 2015, The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.android.testing.uiautomator.BasicSample;

import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.runner.RunWith;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.support.test.InstrumentationRegistry;
import android.support.test.espresso.assertion.ViewAssertions;
import android.support.test.filters.SdkSuppress;
import android.support.test.runner.AndroidJUnit4;
import android.support.test.uiautomator.By;
import android.support.test.uiautomator.UiDevice;
import android.support.test.uiautomator.UiObject;
import android.support.test.uiautomator.UiObject2;
import android.support.test.uiautomator.UiObjectNotFoundException;
import android.support.test.uiautomator.UiScrollable;
import android.support.test.uiautomator.UiSelector;
import android.support.test.uiautomator.Until;
import android.util.Log;

import com.example.android.searchabledict.SearchableDictionary;

import static android.support.test.espresso.Espresso.onView;
import static android.support.test.espresso.matcher.ViewMatchers.isDisplayed;
import static android.support.test.espresso.matcher.ViewMatchers.withText;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.CoreMatchers.nullValue;
import static org.junit.Assert.assertThat;

/**
 * Basic sample for unbundled UiAutomator.
 */
@RunWith(AndroidJUnit4.class)
@SdkSuppress(minSdkVersion = 18)
public class DictionaryBehaviorTest {

    private static final String BASIC_SAMPLE_PACKAGE = "com.example.android.searchabledict";
    private static final int LAUNCH_TIMEOUT = 5000;
    private UiDevice mDevice;

    @Before
    public void startMainActivityFromHomeScreen() {
        // Initialize UiDevice instance
        mDevice = UiDevice.getInstance(InstrumentationRegistry.getInstrumentation());

        // Start from the home screen
        mDevice.pressHome();

        // Wait for launcher
        final String launcherPackage = getLauncherPackageName();
        assertThat(launcherPackage, notNullValue());
        mDevice.wait(Until.hasObject(By.pkg(launcherPackage).depth(0)), LAUNCH_TIMEOUT);

        // Launch the application
        Context context = InstrumentationRegistry.getContext();
        final Intent intent = context.getPackageManager()
                .getLaunchIntentForPackage(BASIC_SAMPLE_PACKAGE);
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);    // Clear out any previous instances
        context.startActivity(intent);

        // Wait for the app to appear
        mDevice.wait(Until.hasObject(By.pkg(BASIC_SAMPLE_PACKAGE).depth(0)), LAUNCH_TIMEOUT);
    }

    @Rule
    public final ActivityRule<SearchableDictionary> main = new ActivityRule<>(SearchableDictionary.class);

    @Test
    public void shouldBeAbleToLaunchMainScreen() {
        onView(withText("Searchable Dictionary")).check(ViewAssertions.matches(isDisplayed()));
    }

    @Test
    public void checkPreconditions() {
        assertThat(mDevice, notNullValue());
    }

    @Test
    public void testDefinition() {


        UiObject2 searchBut = mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "search")), 1000);
        searchBut.click();

        mDevice.wait(Until.findObject(By.textContains("Search the dictionary")), 3000).click();
        mDevice.wait(Until.findObject(By.textContains("Search the dictionary")), 1000).setText("analogy");

        mDevice.pressEnter();

        UiObject2 definition = mDevice
                .wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "definition")),
                        1000 );

        assertThat(definition.getText(), is(equalTo("n. drawing a comparison in order to show a similarity in some respect")));
    }

    @Test
    public void testDefinitionAndGoBack() {


        UiObject2 searchBut = mDevice.wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "search")), 1000);
        searchBut.click();

        mDevice.wait(Until.findObject(By.textContains("Search the dictionary")), 3000).click();
        mDevice.wait(Until.findObject(By.textContains("Search the dictionary")), 3000).setText("analyze");

        mDevice.pressEnter();

        UiObject2 definition = mDevice
                .wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "definition")),
                        1000 );

        definition.click();

        mDevice.wait(Until.findObject(By.descContains("Navigate up")), 5000).click();
        mDevice.wait(Until.findObject(By.descContains("Navigate up")), 5000).click();



        UiObject2 definition2 = mDevice
                .wait(Until.findObject(By.res(BASIC_SAMPLE_PACKAGE, "definition")),
                        1000);

        assertThat(definition2.getText(), is(equalTo("v. break down into components or essential features")));
    }

    /**
     * Uses package manager to find the package name of the device launcher. Usually this package
     * is "com.android.launcher" but can be different at times. This is a generic solution which
     * works on all platforms.`
     */
    private String getLauncherPackageName() {
        // Create launcher Intent
        final Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        // Use PackageManager to get the launcher package name
        PackageManager pm = InstrumentationRegistry.getContext().getPackageManager();
        ResolveInfo resolveInfo = pm.resolveActivity(intent, PackageManager.MATCH_DEFAULT_ONLY);
        return resolveInfo.activityInfo.packageName;
    }
}

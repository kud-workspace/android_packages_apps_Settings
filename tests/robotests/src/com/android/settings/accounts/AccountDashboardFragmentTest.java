/*
 * Copyright (C) 2016 The Android Open Source Project
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
 * limitations under the License
 */
package com.android.settings.accounts;

import static com.android.settings.accounts.AccountDashboardFragmentTest
        .ShadowAuthenticationHelper.LABELS;
import static com.google.common.truth.Truth.assertThat;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.verify;

import android.app.Activity;
import android.content.Context;
import android.os.UserHandle;
import android.provider.SearchIndexableResource;
import android.text.TextUtils;

import com.android.settings.TestConfig;
import com.android.settings.dashboard.SummaryLoader;
import com.android.settingslib.accounts.AuthenticatorHelper;
import com.android.settingslib.drawer.CategoryKey;

import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.MockitoAnnotations;
import org.robolectric.Robolectric;
import org.robolectric.RobolectricTestRunner;
import org.robolectric.annotation.Config;
import org.robolectric.annotation.Implementation;
import org.robolectric.annotation.Implements;
import org.robolectric.shadows.ShadowApplication;

import java.util.List;

@RunWith(RobolectricTestRunner.class)
@Config(manifest = TestConfig.MANIFEST_PATH, sdk = TestConfig.SDK_VERSION)
public class AccountDashboardFragmentTest {

    private AccountDashboardFragment mFragment;

    @Before
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        mFragment = new AccountDashboardFragment();
    }

    @Test
    public void testCategory_isAccount() {
        assertThat(mFragment.getCategoryKey()).isEqualTo(CategoryKey.CATEGORY_ACCOUNT);
    }

    @Test
    @Config(shadows = {
            ShadowAuthenticationHelper.class
    })
    public void updateSummary_shouldDisplayUpTo3AccountTypes() {
        final SummaryLoader loader = mock(SummaryLoader.class);
        final Activity activity = Robolectric.buildActivity(Activity.class).setup().get();

        final SummaryLoader.SummaryProvider provider = mFragment.SUMMARY_PROVIDER_FACTORY
                .createSummaryProvider(activity, loader);
        provider.setListening(true);

        verify(loader).setSummary(provider, LABELS[0] + ", " + LABELS[1] + ", " + LABELS[2]);
    }

    @Test
    public void testSearchIndexProvider_shouldIndexResource() {
        final List<SearchIndexableResource> indexRes =
                AccountDashboardFragment.SEARCH_INDEX_DATA_PROVIDER.getXmlResourcesToIndex(
                        ShadowApplication.getInstance().getApplicationContext(),
                        true /* enabled */);

        assertThat(indexRes).isNotNull();
        assertThat(indexRes.get(0).xmlResId).isEqualTo(mFragment.getPreferenceScreenResId());
    }

    @Implements(AuthenticatorHelper.class)
    public static class ShadowAuthenticationHelper {

        static final String[] TYPES = new String[] {"type1", "type2", "type3", "type4"};
        static final String[] LABELS = new String[] {"LABEL1", "LABEL2",
                "LABEL3", "LABEL4"};

        public void __constructor__(Context context, UserHandle userHandle,
                AuthenticatorHelper.OnAccountsUpdateListener listener) {

        }

        @Implementation
        public String[] getEnabledAccountTypes() {
            return TYPES;
        }

        @Implementation
        public CharSequence getLabelForType(Context context, final String accountType) {
            if (TextUtils.equals(accountType, TYPES[0])) {
                return LABELS[0];
            } else if (TextUtils.equals(accountType, TYPES[1])) {
                return LABELS[1];
            } else if (TextUtils.equals(accountType, TYPES[2])) {
                return LABELS[2];
            } else if (TextUtils.equals(accountType, TYPES[3])) {
                return LABELS[3];
            }
            return "no_label";
        }
    }
}
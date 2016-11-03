/*
 * Copyright 2013 Google Inc. All Rights Reserved.
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

package edu.asu.giles.aspects.access.openid.google;

import java.io.IOException;
import java.security.GeneralSecurityException;
import java.util.Arrays;
import java.util.List;

import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import com.google.api.client.http.javanet.NetHttpTransport;
import com.google.api.client.json.JsonFactory;
import com.google.api.client.json.gson.GsonFactory;

import edu.asu.giles.tokens.IApiTokenContents;
import edu.asu.giles.tokens.impl.ApiTokenContents;

public class Checker {

    private final List<String> mClientIDs;
    private final List<String> audienceList;
    private final GoogleIdTokenVerifier mVerifier;
    private final JsonFactory mJFactory;
    
    public Checker(String[] clientIDs, String[] audiences) {
        mClientIDs = Arrays.asList(clientIDs);
        audienceList = Arrays.asList(audiences);
        NetHttpTransport transport = new NetHttpTransport();
        mJFactory = new GsonFactory();
        mVerifier = new GoogleIdTokenVerifier(transport, mJFactory);
    }

    public CheckerResult check(String tokenString) throws GeneralSecurityException,
            IOException {
        GoogleIdToken token = GoogleIdToken.parse(mJFactory, tokenString);
        if (mVerifier.verify(token)) {
            CheckerResult result = new CheckerResult();
            GoogleIdToken.Payload tempPayload = token.getPayload();
            IApiTokenContents contents = new ApiTokenContents();
            contents.setUsername(tempPayload.getSubject());
            contents.setExpired(true);
            result.setPayload(contents);
            
            if (!audienceList.contains(tempPayload.getAudience())) {
                result.setResult(ValidationResult.AUDIENCE_MISMATCH);
            } else if (!mClientIDs.contains(tempPayload.getAuthorizedParty())) {
                result.setResult(ValidationResult.CLIENT_ID_MISMATCH);
            } else {
                result.setResult(ValidationResult.VALID);
            }
            
            return result;
        }
        
        return null;
    }
    
    
}
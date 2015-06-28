/* Copyright 2009-2011 Vladimir Schaefer
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
package org.springframework.security.saml.parser;

import org.opensaml.xml.parse.ParserPool;

/**
 * Class is initialized from the Spring context and allows retrieval of the ParserPool for code
 * not managed by Spring.
 *
 * @author Vladimir Schaefer
 */
public class ParserPoolHolder {

    /**
     * Pool instance.
     */
    private static ParserPool pool;

    /**
     * Initializes the static parserPool property and makes it available for getPool calls.
     * In case the pool was already previously initialized the last value will be overwritten.
     *
     * @param pool pool to initialize the static property wih
     */
    public ParserPoolHolder(ParserPool pool) {
        ParserPoolHolder.pool = pool;
    }

    /**
     * @return parserPool or null if pool wasn't initialized
     */
    public static ParserPool getPool() {
        return pool;
    }

}
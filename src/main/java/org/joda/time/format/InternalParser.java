/*
 *  Copyright 2001-2014 Stephen Colebourne
 *
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */
package org.joda.time.format;

/**
 * Internal interface for parsing textual representations of datetimes.
 * <p>
 * This has been separated from {@link DateTimeParser} to change to using
 * {@code CharSequence}.
 *
 * @author Stephen Colebourne
 * @since 2.4
 * @deprecated Use org.joda.time.format.parsing.InternalParser instead.
 */
@Deprecated
interface InternalParser extends org.joda.time.format.parsing.InternalParser {
}

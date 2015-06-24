/*
 * Copied from the DnsJava project
 *
 * Copyright (c) 1998-2011, Brian Wellington.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *   * Redistributions of source code must retain the above copyright notice,
 *     this list of conditions and the following disclaimer.
 *
 *   * Redistributions in binary form must reproduce the above copyright notice,
 *     this list of conditions and the following disclaimer in the documentation
 *     and/or other materials provided with the distribution.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */

package io.milton.dns.record;

import io.milton.dns.Name;

import java.util.*;

/**
 * Signature - A SIG provides the digital signature of an RRset, so that
 * the data can be authenticated by a DNSSEC-capable resolver.  The
 * signature is usually generated by a key contained in a KEYRecord
 * @see RRset
 * @see DNSSEC
 * @see KEYRecord
 *
 * @author Brian Wellington
 */

public class SIGRecord extends SIGBase {

private static final long serialVersionUID = 4963556060953589058L;

SIGRecord() {}

Record
getObject() {
	return new SIGRecord();
}

/**
 * Creates an SIG Record from the given data
 * @param covered The RRset type covered by this signature
 * @param alg The cryptographic algorithm of the key that generated the
 * signature
 * @param origttl The original TTL of the RRset
 * @param expire The time at which the signature expires
 * @param timeSigned The time at which this signature was generated
 * @param footprint The footprint/key id of the signing key.
 * @param signer The owner of the signing key
 * @param signature Binary data representing the signature
 */
public
SIGRecord(Name name, int dclass, long ttl, int covered, int alg, long origttl,
	  Date expire, Date timeSigned, int footprint, Name signer,
	  byte [] signature)
{
	super(name, Type.SIG, dclass, ttl, covered, alg, origttl, expire,
	      timeSigned, footprint, signer, signature);
}

}

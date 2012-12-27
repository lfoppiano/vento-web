package org.vento.crawler.amazon.utility

import java.security.SignatureException
import javax.crypto.Mac
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base64

public class Reviews {

    private static final String HMAC_SHA256_ALGORITHM = "HmacSHA256";

    /**
     * Prepare request for the amazon webservice query and sign the data. Example of URL
     * is "http://webservices.amazon.com/onca/xml?Service=AWSECommerceService&AWSAccessKeyId=AKIAIM63W46EUMNAPYYQ&Operation=ItemLookup&ItemId=B005890FUI&ResponseGroup=Reviews&TruncateReviewsAt=\"256\"&IncludeReviewsSummary=\"False\"&Version=2011-08-01&Timestamp=2012-14-02T12:00:00Z"
     *
     * @param hostName host name related to the query
     * @param url url related to the webservice called (default for amaazon is http://webservices.amazon.com/onca/xml)
     * @param amazonSecretKey the amazon secret key
     * @param parameters the parameters of the amazon
     * @return
     */
    def static prepareRequest(String url, Map parameters, String amazonSecretKey) {

        def canQueryArray = []
        def canQueryString = ''

        parameters = prepareParameters(parameters, new Date())

        parameters.each {canQueryArray << "${it.key}=${it.value}"}
        canQueryString=canQueryArray.join("&")

        return "${url}?${canQueryString}&Signature=${signData('GET'+"\n"+'webservices.amazon.com'+"\n"+'/onca/xml'+"\n"+canQueryString,amazonSecretKey)}"
    }

    /**
     * Complete the parameters, sort and encode them
     * @param parameters
     * @return
     */
    private static def prepareParameters(Map parameters, Date now) {

        parameters.put('SignatureVersion','2')
        parameters.put('SignatureMethod',HMAC_SHA256_ALGORITHM)
        parameters.put('Timestamp', "${now.format('yyyy-MM-dd',TimeZone.getTimeZone('GMT'))}T${now.format('HH:mm:ss',TimeZone.getTimeZone('GMT'))}Z")

        def sortEncParams = [:]

        parameters.sort().each{
            sortEncParams.put(URLEncoder.encode(it.key).replace('+', '%20'),URLEncoder.encode(it.value).replace('+', '%20'))
        }

        return sortEncParams
    }


    private static def signData(String data, String key){

        String result;

        try {

            // get an hmac_sha1 key from the raw key bytes
            SecretKeySpec signingKey = new SecretKeySpec(key.getBytes(), HMAC_SHA256_ALGORITHM);

            // get an hmac_sha1 Mac instance and initialize with the signing key
            Mac mac = Mac.getInstance(HMAC_SHA256_ALGORITHM);
            mac.init(signingKey);

            // compute the hmac on inputStream data bytes
            byte[] rawHmac = mac.doFinal(data.getBytes());

            // base64-encode the hmac
            result = Base64.encodeBase64String(rawHmac);

        } catch (Exception e) {
            throw new SignatureException("Failed to generate HMAC : " + e.getMessage());
        }

        return URLEncoder.encode(result)

    }
}

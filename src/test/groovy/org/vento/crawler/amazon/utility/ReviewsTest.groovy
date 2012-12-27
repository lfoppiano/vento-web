package org.vento.crawler.amazon.utility;

import org.junit.Test

public class ReviewsTest {

    @Test
    public void testPrepareParameters() throws Exception {
        Reviews reviews = new Reviews();

        def tempDate = new Date()
        def timestamp = URLEncoder.encode("${tempDate.format('yyyy-MM-dd',TimeZone.getTimeZone('GMT'))}T${tempDate.format('HH:mm:ss',TimeZone.getTimeZone('GMT'))}Z")

        def parameters = [
                'Timestamp': '2011:12  → 2- 1222',
                'Service': 'AWSECommerceService',
                'Operation': 'ItemLookup',
                'ItemId':'B005890FUI',
                'ResponseGroup':'Reviews',
                'TruncateReviewsAt':'"256"',
                'IncludeReviewsSummary':'"False"',
                'Version':'2011-08-01']

        def expectedParameters = [
                'IncludeReviewsSummary':'%22False%22',
                'ItemId':'B005890FUI',
                'Operation':'ItemLookup',
                'ResponseGroup':'Reviews',
                'Service':'AWSECommerceService',
                'SignatureMethod' : 'HmacSHA256',
                'SignatureVersion' : '2',
                'Timestamp': timestamp,
                'TruncateReviewsAt':'%22256%22',
                'Version':'2011-08-01']

        def outMap = reviews.prepareParameters(parameters, tempDate)
        
        assert outMap.size() == expectedParameters.size()
        assert outMap.equals(expectedParameters)
    }
}

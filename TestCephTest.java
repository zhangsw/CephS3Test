/**
 * Created by pt on 2016/7/11.
 */
import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.auth.SystemPropertiesCredentialsProvider;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.model.*;
import org.apache.commons.logging.Log;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

/**
 * @author swzhang
 * @create 2016/4/14.
 */



public class TestCephTest {

    private static final String accessKey = "FF6KMKI1HXG72BAM9JFF";
    private static final String secretKey = "IoWAj3iBIyvog8AhEh2cJ4iPjyvxZAdmBJbMNlZj";
    private static final String  IP =  "http://10.254.9.20:7480";

    private static final Logger LOG = Logger.getLogger(TestCephTest.class);
    private ClientConfiguration opts = new ClientConfiguration();
    private static final AWSCredentials credentials = new BasicAWSCredentials(accessKey, secretKey);
    private static  AmazonS3 conn;

    @Before
    public void setUp() throws Exception {
        conn = new AmazonS3Client(credentials,opts);
        conn.setEndpoint(IP);
    }

    @Test
    /*
    创建1000个容器
    caseID:bucket_create_normal_1
    */
    public void testS3BucketCreateNormal1(){
        System.out.println("caseID: bucket_create_normal_1");
        int max = 1000;
        bucketCreateDelete(max,"create");
        Assert.assertEquals("caseID:bucket_create_normal_2  Fail",conn.listBuckets().size(),max);
        bucketCreateDelete(max,"delete");
    }

    public void bucketCreateDelete(int max,String model){
        for (int n=0;n< max ;n++) {
            String bucketName = "auto-bucket-create-" + n;
            if (model == "create"){
                conn.createBucket(bucketName);
            }
            else{
                conn.deleteBucket(bucketName);
            }
        }
    }

       @Test
       /*
       创建名字为3个字符的容器
       caseID:bucket_create_normal_2
       */
       public void testS3BucketCreateNormal2() {
           System.out.println("caseID:bucket_create_normal_2");
           String bucketName = "aut";
           Bucket bucket = conn.createBucket(bucketName);
           Assert.assertTrue("caseID:bucket_create_normal_2 Fail",conn.listBuckets().toString().contains(bucketName));
           conn.deleteBucket(bucketName);
       }

    @Test
    /*
    创建名字为10个字符的容器
    caseID:S3_bucket_create_normal_3
    */
    public void testS3BucketCreateNormal3(){
        System.out.println("caseID:S3_bucket_create_normal_3");
        String bucketName = "auto-bucket";
        conn.createBucket(bucketName);
        Assert.assertTrue("caseID:S3_bucket_create_normal_3 Fail",conn.listBuckets().toString().contains(bucketName));
        conn.deleteBucket(bucketName);
    }

    @Test
    /*
    创建名字为63个字符的容器
    caseID:S3_bucket_create_normal_4
    */
    public void testS3BucketCreateNormal4(){
        System.out.println("caseID:S3_bucket_create_normal_4");
        String bucketName = "auto-bucket-";
        for (int i=1;i<52;i++){
            bucketName += "s";
        }
        conn.createBucket(bucketName);
        Assert.assertTrue("caseID:S3_bucket_create_normal_3 Fail",conn.listBuckets().toString().contains(bucketName));
        conn.deleteBucket(bucketName);
    }

    @Test
    /*
     创建名字含有"."的容器
     caseID:S3_bucket_create_normal_5
     */
    public void testS3BucketCreateNormal5(){
        System.out.println("caseID:S3_bucket_create_normal_5");
        String bucketName = "auto.bucket";
        conn.createBucket(bucketName);
        Assert.assertTrue("caseID:S3_bucket_create_normal_3 Fail",conn.listBuckets().toString().contains(bucketName));
        conn.deleteBucket(bucketName);
    }

    @Test
    /*
    创建1001个容器(最大为1000)
    S3_bucket_create_abnormal_1
    */
    public void testS3BucketCreateAbNormal1(){
        System.out.println("caseID: S3_bucket_create_abnormal_1");
        int max = 1000;
        String bucketName = "auto-create-bucket";
        bucketCreateDelete(max,"create");
        try{
            conn.createBucket(bucketName);
        }
        catch (Exception e){
            Assert.assertTrue("caseID:S3_bucket_create_normal_3 Fail",e.getMessage().toString().contains("TooManyBuckets"));
        }
        bucketCreateDelete(max,"delete");
    }

    @Test
    /*
    * 同一个用户创建同名容器
    * caseID:S3_bucket_create_abnormal_2
    * */
    public void testS3BucketCreateAbNormal2(){
        System.out.println("caseID: S3_bucket_create_abnormal_2");
        String bucketName = "auto-bucket-create-abnormal-2";
        conn.createBucket(bucketName);
        try{
            conn.createBucket(bucketName);
        }
        catch (Exception e) {
            Assert.assertTrue("caseID:S3_bucket_create_abnormal_2   Fail",e.getMessage()=="");
        }
        conn.deleteBucket(bucketName);
    }

    @Test
    /*
    * 创建名字包含下划线的容器
    * caseID:S3_bucket_create_abnormal_6
    * */
    public void testS3BucketCreateAbNormal6(){
        System.out.println("caseID: S3_bucket_create_abnormal_6");
        String bucketName = "auto-bucket-create-abnormal_6";
        try{
            conn.createBucket(bucketName);
        }
        catch  (Exception e){
            Assert.assertTrue("caseID:S3_bucket_create_abnormal_6   Fail",e.getMessage().toString().contains("not contain '_'"));
        }

    }
    @Test
    /*
    * 创建名字尾部为下划线的容器
    * caseID:S3_bucket_create_abnormal_7
    * */
    public void testS3BucketCreateAbNormal7(){
        System.out.println("caseID: S3_bucket_create_abnormal_7");
        String bucketName = "auto-bucket-create-abnormal-7_";
        try{
            conn.createBucket(bucketName);
        }
        catch (Exception e){
//            System.out.println(e.getMessage());
            Assert.assertTrue("caseID:S3_bucket_create_abnormal_7   Fail",e.getMessage().toString().contains("not contain '_'"));
        }
    }

    @Test
    /*
    * 创建名字为空的容器
    * S3_bucket_create_abnormal_8
    * */
    public void testS3BucketCreateAbNormal8(){
        System.out.println("caseID: S3_bucket_create_abnormal_8");
        String bucketName = "";
        try{
            conn.createBucket(bucketName);
        }
        catch (Exception e){
            Assert.assertTrue("caseID:S3_bucket_create_abnormal_7   Fail",e.getMessage().toString().contains("between 3 and 63 characters long"));
        }
    }

    @Test
    /*
    * 创建名字为1个字符的容器
    * S3_bucket_create_abnormal_9
    * */
    public void testS3BucketCreateAbNormal9(){
        System.out.println("caseID:S3_bucket_create_abnormal_9");
        String bucketName = "a";
        try{
            conn.createBucket(bucketName);
        }
        catch (Exception e){
            Assert.assertTrue("caseID:S3_bucket_create_abnormal_9   Fail",e.getMessage().toString().contains("between 3 and 63 characters long"));
        }
    }

    @Test
    /*
    * 创建名字为2个字符的容器
    * S3_bucket_create_abnormal_10
    * */
    public void testS3BucketCreateAbNormal10(){
        System.out.println("caseID:S3_bucket_create_abnormal_10");
        String bucketName = "aa";
        try{
            conn.createBucket(bucketName);
        }
        catch (Exception e){
            Assert.assertTrue("caseID:S3_bucket_create_abnormal_10   Fail",e.getMessage().toString().contains("between 3 and 63 characters long"));
        }
    }

    @Test
    /*
    * 创建名字为64个字符的容器
    * S3_bucket_create_abnormal_11
    * */
    public void testS3BucketCreateAbNormal11(){
        System.out.println("caseID:S3_bucket_create_abnormal_11");
        String bucketName = "";
        for (int i=1;i<=64;i++){
            bucketName += "s";
        }
        try{
            conn.createBucket(bucketName);
        }
        catch (Exception e){
            Assert.assertTrue("caseID:S3_bucket_create_abnormal_11   Fail",e.getMessage().toString().contains("between 3 and 63 characters long"));
        }
    }

    @Test
    /*
    * 创建名字为192.168.1.2的容器
    * S3_bucket_create_abnormal_12
    * */
    public void testS3BucketCreateAbNormal12(){
        System.out.println("caseID:S3_bucket_create_abnormal_12");
        String bucketName = "192.168.1.2";
        try{
            conn.createBucket(bucketName);
        }
        catch (Exception e){
            Assert.assertTrue("caseID:S3_bucket_create_abnormal_12   Fail",e.getMessage().toString().contains("not be formatted as an IP Address"));
        }
    }

    @Test
    /*
    * 创建名字含有感叹号的容器
    * S3_bucket_create_abnormal_13
    * */
    public void testS3BucketCreateAbNormal13(){
        System.out.println("caseID:S3_bucket_create_abnormal_13");
        String bucketName = "auto-bucket-create-1!3";
        try{
            conn.createBucket(bucketName);
        }
        catch (Exception e){
            Assert.assertTrue("caseID:S3_bucket_create_abnormal_13   Fail",e.getMessage().toString().contains("not contain '!'"));
        }
    }
    public void println(String message){
        System.out.println(message);
    }

    @Test
    /*
    * 创建名字含有".."的容器
    * S3_bucket_create_abnormal_14
    * */
    public void testS3BucketCreateAbNormal14(){
        System.out.println("caseID:S3_bucket_create_abnormal_14");
        String bucketName = "auto-bucket-create..14";
        try{
            conn.createBucket(bucketName);
        }
        catch (Exception e){
            Assert.assertTrue("caseID:S3_bucket_create_abnormal_14   Fail",e.getMessage().toString().contains("not contain two adjacent periods"));
        }
    }

    @Test
    /*
    * 创建名字含有"-."的容器
    * S3_bucket_create_abnormal_15
    * */
    public void testS3BucketCreateAbNormal15(){
        System.out.println("caseID:S3_bucket_create_abnormal_15");
        String bucketName = "auto-bucket-create-.15";
        try{
            conn.createBucket(bucketName);
        }
        catch (Exception e){
            Assert.assertTrue("caseID:S3_bucket_create_abnormal_15   Fail",e.getMessage().toString().contains("not contain dashes next to periods"));
        }
    }

    @Test
    /*
    * 创建名字含有".-"的容器
    * S3_bucket_create_abnormal_16
    * */
    public void testS3BucketCreateAbNormal16(){
        println("caseID:S3_bucket_create_abnormal_16");
        String bucketName = "auto-bucket-create.-16";
        try{
            conn.createBucket(bucketName);
        }
        catch (Exception e){
            Assert.assertTrue("caseID:S3_bucket_create_abnormal_16  Fail.",e.getMessage().contains("not contain dashes next to periods"));
        }
    }

    @Test
    /*
    * 创建名字含有中文的容器
    * S3_bucket_create_abnormal_17
    * */
    public void testS3BucketDelNormal1(){
        println("caseID:S3_bucket_create_abnormal_17");
        String bucketName="容器1";
        try{
            conn.createBucket(bucketName);
        }
        catch (Exception e){
            Assert.assertTrue("caseID:S3_bucket_create_abnormal_17  Fail",e.getMessage().contains("not contain '容'"));
        }

    }

    @Test
    /*
    * 删除一个有对象的容器
    * S3_bucket_del_abnormal_1
    * */
    public void testS3BucketDelAbNormal1(){

    }
    @Test
    /*
    * 删除一个不存在的容器
    * S3_bucket_del_abnormal_2
    * */
    public void testS3BucketDelAbNormal2(){
        println("caseID:S3_bucket_del_abnormal_2");
        String bucketName = "auto-bucket-name-no-exist";
        try{
            conn.deleteBucket(bucketName);
        }
        catch (Exception e){
            Assert.assertTrue("",e.getMessage().contains("NoSuchBucket"));
        }
    }

    @Test
    /*
    * 有容器的情况，列出所有容器
    * S3_bucket_list_normal_1
    * */
    public void testS3BucketListNormal1(){
        println("caseID:S3_bucket_list_normal_1");

        int num = 10;
        for (int i=1;i<num;i++){
            String bucketName = "auto-bucket-" + i;
            conn.createBucket(bucketName);
        }
        Assert.assertTrue("caseID:S3_bucket_list_normal_1",conn.listBuckets().size()==num);
        for (int j=1;j<num;j++){
            String bucketName = "auto-bucket-" + j;
            conn.deleteBucket(bucketName);
        }
    }

    @Test
    /*
    * 没有容器的情况下，列出所有容器
    * S3_bucket_list_normal_2
    * */
    public void testS3BucketListNormal2(){
        println("caseID:S3_bucket_list_normal_2");
    }

    @Test
    /*
    * 容器中无对象情况，列出所有对象
    * S3_bucket_list_normal_3
    * */
    public void testS3BucketListNormal3(){
        println("caseID:testS3BucketListNormal3");

        String bucketName = "auto-bucket-list-normal-3";
        conn.createBucket(bucketName);
        Assert.assertTrue("caseID:S3_bucket_list_normal_3   Fail",conn.listObjects(bucketName).getObjectSummaries().size()==0);
        conn.deleteBucket(bucketName);
    }

    @Test
    /*
    * 容器中3个对象情况，列出所有对象
    * S3_bucket_list_normal_4
    * */
    public void testS3BucketListNormal4(){
        println("caseID:S3_bucket_list_normal_4");

        String bucketName = "auto-bucket-list-normal-4";
        conn.createBucket(bucketName);
//        InputStream input =
//        conn.putObject(bucketName,InputStre,"");

    }

/*

//ff
    @Test
    //test
    public void testException(){
        try{
            int a = 0;
            int b = 3;
            int c = b /a ;
        }
        catch  (Exception e){

            System.out.println(e.getMessage());
        }
    }


    @Test
    //创建Num个容器(num=max+1),
    public void testS3BucketCreateMaxAddOne(){
        String aKey = "wc";
        String sKey = "wc";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        String s ;
        int n;
        for (n=0;n<10;n++) {
            s = "tdk-bucket-test-" + n;
//            Bucket bucket = conn1.createBucket(s);
            conn1.deleteBucket(s);
        }
//        conn1.createBucket("tdk-bucket-test-11");
        int i ;
        List<Bucket> bucketList = conn1.listBuckets();
        for (i = 0 ;i<bucketList.size();i++)
        {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }
    @Test
    //不同用户，创建同名容器
    public void testS3BucketCreateTwoUserSameName(){
        String aKey = "wc";
        String sKey = "wc";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        String s = "tdk-bucket-test-1";
//        conn.createBucket(s);
        conn1.createBucket(s);
        int i ;
        List<Bucket> bucketList = conn1.listBuckets();
        for (i = 0 ;i<bucketList.size();i++)
        {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //bucket名字以下划线开头
    public void testS3BucketCreateNamingStartUnderscore(){
        Bucket bucket = conn.createBucket("_tdk_test_1");
        int i ;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0 ;i<bucketList.size();i++)
        {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //名字包含下划线
    public void testS3BucketCreateNamingCenterUnderscore(){
        Bucket bucket1 = conn.createBucket("tdk_test_1");
        int i ;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0 ;i<bucketList.size();i++)
        {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //名字尾部为下划线
    public void testS3BucketCreateNamingEndUnderscore(){
        Bucket bucket1 = conn.createBucket("tdk_test_");
        int i ;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0 ;i<bucketList.size();i++)
        {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //bucket名字为空
    public void testS3BucketCreateNamingEmpty(){

//        Bucket bucket1 = conn.createBucket("");
        int i ;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0 ;i<bucketList.size();i++)
        {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //bucket名字1个字符
    public void testS3BucketCreateNamingOneChar() {

        Bucket bucket = conn.createBucket("a");
        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }
    @Test
    //bucket名字个2字符
     public void testS3BucketCreateNamingTwoChar() {
        Bucket bucket = conn.createBucket("aa");
        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //bucket名字为3个字符串
    public void testS3BucketCreateNaming3Char() {
        //创建3个字符串
        String s="" ;
        int num ;
        int max = 3;
        for (num = 0;num < max ; num++) {
            s += "a";
        }
        System.out.print(s.length() + "\n" );

        //创建bucket
        Bucket bucket = conn.createBucket(s);

        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
        conn.deleteBucket(s);
    }

    @Test
    //bucket名字为10个字符串
    public void testS3BucketCreateNaming10Char() {
        //创建10个字符串
        String s="" ;
        int num ;
        int max = 10;
        for (num = 0;num < max ; num++) {
            s += "a";
        }
        System.out.print(s.length() + "\n" );

        //创建bucket
        Bucket bucket = conn.createBucket(s);

        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
        conn.deleteBucket(s);
    }

    @Test
    //bucket名字为63个字符串
    public void testS3BucketCreateNaming63Char() {
        //创建63个字符串
        String s="" ;
        int num ;
        int max = 63;
        for (num = 0;num < max ; num++) {
            s += "a";
        }
        System.out.print(s.length() + "\n" );

        //创建bucket
        Bucket bucket = conn.createBucket(s);

        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
        conn.deleteBucket(s);
    }

    @Test
    //bucket名字为64个字符串
    public void testS3BucketCreateNaming64Char() {
        //创建63个字符串
        String s="" ;
        int num ;
        int max = 64;
        for (num = 0;num < max ; num++) {
            s += "a";
        }
        System.out.print(s.length() + "\n" );

        //创建bucket
        Bucket bucket = conn.createBucket(s);

        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //bucket名字为256个字符串
    public void testS3BucketCreateNaming256Char() {
        //创建63个字符串
        String s="" ;
        int num ;
        int max = 256;
        for (num = 0;num < max ; num++) {
            s += "a";
        }

        System.out.print(s.length() + "\n" );

        //创建bucket
        Bucket bucket = conn.createBucket(s);

        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //bucket名字为IP :"192.168.1.2"
    public void testS3BucketCreateNamingIP() {
        String s="192.168.1.2" ;
        System.out.print(s + "\n" );

        //创建bucket
        Bucket bucket = conn.createBucket(s);

        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //bucket名字包含标点符号
    public void testS3BucketCreateNamingContainPunctuation() {
        //创建bucket
        Bucket bucket = conn.createBucket("abc!aaa");

        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //bucket名字包1个点“.”
    public void testS3BucketCreateNamingContainDot() {
        //创建bucket
        String s = "tdk.bucket";
        Bucket bucket = conn.createBucket(s);

        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
        conn.deleteBucket(s);
    }

    @Test
    //bucket名字包2个点“..”
    public void testS3BucketCreateNamingContainDotDot() {
        //创建bucket
        String s = "tdk..bucket";
        Bucket bucket = conn.createBucket(s);

        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //bucket名字包“.-”
    public void testS3BucketCreateNamingContainDotDash() {
        //创建bucket
        String s = "tdk.-bucket";
        Bucket bucket = conn.createBucket(s);

        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }
    @Test
    //bucket名字包“-.”
    public void testS3BucketCreateNamingContainDashDot() {
        //创建bucket
        String s = "tdk-.bucket";
        Bucket bucket = conn.createBucket(s);

        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }
    @Test
    //bucket名字包中文
    public void testS3BucketCreateNamingContainChinese() {
        //创建bucket
        String s = "tdk-bucket-中文";
        Bucket bucket = conn.createBucket(s);

        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //bucket已经存在
    public void testS3BucketCreateNamingExit() {
        //创建bucket
        String s = "tdk-bucket";
        Bucket bucket = conn.createBucket(s);
        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
        Bucket bucket1 = conn.createBucket(s);

        conn.deleteBucket(s);
    }

    @Test
    //删除bucket
    public void testS3BucketDelete() {
        //创建bucket
        String s = "tdk-bucket-1";
        Bucket bucket = conn.createBucket(s);

        conn.deleteBucket(s);

        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //删除一个不存在的bucket
    public void testS3BucketDeleteNonexist() {
        int num ;

        //创建bucket
        String s = "tdk-bucket-1";
        Bucket bucket = conn.createBucket(s);
        conn.deleteBucket(s);
        conn.deleteBucket(s);
        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //列出用户创建的所有bucket
    public void testS3BucketListAll() {
//        //创建bucket
//        String s = "tdk-bucket-1";
//        Bucket bucket = conn.createBucket(s);
//        // PutObjectRequest request = new PutObjectRequest("zsw-test-1", "python1.chm", new File("F:\\python.chm"));
//        PutObjectRequest request = new PutObjectRequest("tdk-bucket-1","test/s3cmd",new File("D:\\download\\s3-tests-master.zip"));
//        conn.putObject(request);
//        ListObjectsRequest request1 = new ListObjectsRequest();
//        request1.setBucketName(s);
//        ObjectListing objects = conn.listObjects(request1);

//        ListObjectsRequest request = new ListObjectsRequest();
//        request.setBucketName("zsw-test-1");
//        request.setEncodingType("utf-8");
//        ObjectListing objects = conn.listObjects(request);
//        Assert.assertNotNull(objects);
        int n ;
        String s;
        for (n=0 ; n < 3 ;n++){
            s = "tdk-bucket-" + n ;
            conn.createBucket(s);
        }
        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //没有bucket的情况下列出所有bucket
    public void testS3BucketListNonExist() {
        int i;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0; i < bucketList.size(); i++) {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }

    @Test
    //容器为空，列出容器中的对象
    public void testS3BucketListEmpty() {
        PutObjectRequest request = new PutObjectRequest("tdk-bucket-1","test/s3cmd2",new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request);

        conn.createBucket("tdk-bucket-1");
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName("tdk-bucket-1");
        ObjectListing objects = conn.listObjects(request1);
        System.out.print(objects.getObjectSummaries().get(2).getKey());
    }

    @Test
    //bucket中有不少于3个obj，列出所有obj
    public void testS3BucketListObjectAll() {
//        PutObjectRequest request = new PutObjectRequest("tdk-bucket-1","test/s3cmd4",new File("D:\\download\\s3-tests-master.zip"));
//        conn.putObject(request);
        int i;
//        conn.createBucket("tdk-bucket-1");
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName("tdk-bucket-1");
        ObjectListing objects = conn.listObjects(request1);

        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
    }

    @Test
    //列出一个不存在的bucket中的所有obj
    public void testS3BucketListObjectBucketNonExist() {
        int i;
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName("tdk-bucket-no");
        ObjectListing objects = conn.listObjects(request1);

        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
    }

    @Test
    //列出bucket中prefix为test的obj
    public void testS3BucketListObjectPrefix() {
//        PutObjectRequest request = new PutObjectRequest("tdk-bucket-1","qtest/oth",new File("D:\\download\\s3-tests-master.zip"));
//        conn.putObject(request);
        int i;
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName("tdk-bucket-1");
        request1.setPrefix("test");
        ObjectListing objects = conn.listObjects(request1);

        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
    }

    @Test
    //列出bucket中prefix为test的obj,bucket中obj key无以test为prefix
    public void testS3BucketListObjectPrefixNonExist() {
//        PutObjectRequest request = new PutObjectRequest("tdk-bucket-1","qtest/oth",new File("D:\\download\\s3-tests-master.zip"));
//        conn.putObject(request);
        int i;
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName("tdk-bucket-1");
        request1.setPrefix("test");
        ObjectListing objects = conn.listObjects(request1);

        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
    }

    @Test
    //从marker位置开始列出obj
    public void testS3BucketListObjectMarker() {
//        PutObjectRequest request = new PutObjectRequest("tdk-bucket-1","test/s3cmd2",new File("D:\\download\\s3-tests-master.zip"));
//        conn.putObject(request);
        int i;
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName("tdk-bucket-1");
        request1.setMarker("test/s3cmd1");
        ObjectListing objects = conn.listObjects(request1);

        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
    }

    @Test
    //列出2个obj,bucket中有不少于是2个obj
    public void testS3BucketListObjectMaxLe() {
//        PutObjectRequest request = new PutObjectRequest("tdk-bucket-1","test/s3cmd2",new File("D:\\download\\s3-tests-master.zip"));
//        conn.putObject(request);
        int i;
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName("tdk-bucket-1");
        request1.setMaxKeys(2);
        ObjectListing objects = conn.listObjects(request1);

        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
    }

    @Test
    //列出10个obj,bucket中有不大于是2个obj
    public void testS3BucketListObjectMaxGe() {
//        PutObjectRequest request = new PutObjectRequest("tdk-bucket-1","test/s3cmd2",new File("D:\\download\\s3-tests-master.zip"));
//        conn.putObject(request);
        int i;
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName("tdk-bucket-1");
        request1.setMaxKeys(10);
        ObjectListing objects = conn.listObjects(request1);

        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
    }

    @Test
    //每次列出0个obj
    public void testS3BucketListObjectMaxZero() {
//        PutObjectRequest request = new PutObjectRequest("tdk-bucket-1","test/s3cmd2",new File("D:\\download\\s3-tests-master.zip"));
//        conn.putObject(request);
        int i;
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName("tdk-bucket-1");
        request1.setMaxKeys(0);
        ObjectListing objects = conn.listObjects(request1);

        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
    }

    @Test
    //delimiter为“/”，列出obj
    public void testS3BucketListObjectDelimiter() {
        PutObjectRequest request = new PutObjectRequest("tdk-bucket-1","s3cmd5",new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request);
        int i;
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName("tdk-bucket-1");
        request1.setDelimiter("/");
        ObjectListing objects = conn.listObjects(request1);

        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
    }



//        AccessControlList acl = conn.getBucketAcl("zsw-test-1");
//        LOG.info(acl.toString());

//        CreateBucketRequest request = new CreateBucketRequest("zsw-test-1");
//        request.setCannedAcl(CannedAccessControlList.PublicRead);
//        request.setRegion("CN");
//        Bucket bucket = conn.createBucket(request);
//        Assert.assertNotNull(bucket);
//        LOG.info(bucket.toString());

    @Test
    //创建private的bucket
    public void testS3BucketSetPrivate(){
        String s = "tdk-test-private";
        CreateBucketRequest request = new CreateBucketRequest(s);
        request.setCannedAcl(CannedAccessControlList.Private);
        Bucket bucket = conn.createBucket(request);
        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }

    @Test
    //创建public read 的bucket
    public void testS3BucketCreatePublicRead(){
        String s = "tdk-test-public-read";
        CreateBucketRequest request = new CreateBucketRequest(s);
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        Bucket bucket = conn.createBucket(request);
        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }

    @Test
    //创建public read write 的bucket
    public void testS3BucketCreatePublicReadWrite(){
        String s = "tdk-test-public-read-write";
        CreateBucketRequest request = new CreateBucketRequest(s);
        request.setCannedAcl(CannedAccessControlList.PublicReadWrite);
        Bucket bucket = conn.createBucket(request);
        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }

    @Test
    //指定用户tdk对bucket有读权限
    public void testS3BucketCreateUserRead(){
        String s = "tdk-test-read-user";
        CreateBucketRequest request = new CreateBucketRequest(s);
        Bucket bucket = conn.createBucket(request);
        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("tdkf","tdkf");
        list.setOwner(owner);
        CanonicalGrantee grantee = new CanonicalGrantee("tdkf");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request1);

        CanonicalGrantee grantee1 = new CanonicalGrantee("tdk");
        list.grantPermission(grantee1,Permission.Read);
        SetBucketAclRequest request2 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request2);

        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);

    }

    @Test
    //指定用户tdk对bucket有write有写权限
    public void testS3BucketCreateUserWrite(){

        String s = "tdk-test-write-user";
        CreateBucketRequest request = new CreateBucketRequest(s);
        Bucket bucket = conn.createBucket(request);
        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("tdkf","tdkf");
        list.setOwner(owner);
        CanonicalGrantee grantee = new CanonicalGrantee("tdkf");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request1);

        CanonicalGrantee grantee1 = new CanonicalGrantee("tdk");
        list.grantPermission(grantee1,Permission.Write);
        SetBucketAclRequest request2 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request2);

        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }

    @Test
    //指定用户tdk对bucket的acl有read权限
    public void testS3BucketCreateUserReadAcl(){

        String s = "tdk-test-readacl-user";
        CreateBucketRequest request = new CreateBucketRequest(s);
        Bucket bucket = conn.createBucket(request);
        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("tdkf","tdkf");
        list.setOwner(owner);
        CanonicalGrantee grantee = new CanonicalGrantee("tdkf");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request1);

        CanonicalGrantee grantee1 = new CanonicalGrantee("tdk");
        list.grantPermission(grantee1,Permission.ReadAcp);
        SetBucketAclRequest request2 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request2);

        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }

    @Test
    //指定用户tdk对bucket的acl有写权限
    public void testS3BucketCreateUserWriteAcl(){
        String s = "tdk-test-write-acl-user";
        CreateBucketRequest request = new CreateBucketRequest(s);
        Bucket bucket = conn.createBucket(request);
        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("tdkf","tdkf");
        list.setOwner(owner);
        CanonicalGrantee grantee = new CanonicalGrantee("tdkf");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request1);

        CanonicalGrantee grantee1 = new CanonicalGrantee("tdk");
        list.grantPermission(grantee1,Permission.WriteAcp);
        SetBucketAclRequest request2 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request2);

        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }

    @Test
    //指定用户tdk对bucket及bucket acl有读写权限
    public void testS3BucketCreateUserFullControl(){
        String s = "tdk-test-write-acl-user";
        CreateBucketRequest request = new CreateBucketRequest(s);
        Bucket bucket = conn.createBucket(request);
        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("tdkf","tdkf");
        list.setOwner(owner);
        CanonicalGrantee grantee = new CanonicalGrantee("tdkf");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request1);

        CanonicalGrantee grantee1 = new CanonicalGrantee("tdk");
        list.grantPermission(grantee1,Permission.FullControl);
        SetBucketAclRequest request2 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request2);

        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }

    @Test
    //对一个不存在用，设置对bucket的读写权限
    public void testS3BucketCreateUserNonExist(){
        String s = "tdk-test-write-acl-user";
        CreateBucketRequest request = new CreateBucketRequest(s);
        Bucket bucket = conn.createBucket(request);
        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("tdkf","tdkf");
        list.setOwner(owner);
        CanonicalGrantee grantee = new CanonicalGrantee("tdkf");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request1);

        CanonicalGrantee grantee1 = new CanonicalGrantee("testuser");
        list.grantPermission(grantee1,Permission.Write);
        SetBucketAclRequest request2 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request2);

        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }

    @Test
    //删除一个用户对容器的访问权限
    public void testS3BucketUserDel(){
        String s = "tdk-test-use-del";
        CreateBucketRequest request = new CreateBucketRequest(s);
        Bucket bucket = conn.createBucket(request);
        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("tdkf","tdkf");
        list.setOwner(owner);
        CanonicalGrantee grantee = new CanonicalGrantee("tdkf");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request1);

        CanonicalGrantee grantee1 = new CanonicalGrantee("tdk");
        list.grantPermission(grantee1,Permission.Write);
        SetBucketAclRequest request2 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request2);

        AccessControlList list1 = new AccessControlList();
        Owner owner1 = new Owner("tdkf","tdkf");
        list1.setOwner(owner1);
        CanonicalGrantee grantee3 = new CanonicalGrantee("tdkf");
        list1.grantPermission(grantee3,Permission.FullControl);
        SetBucketAclRequest request3 = new SetBucketAclRequest(s,list1);
        conn.setBucketAcl(request3);

        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }

    @Test
    //容器权限设置为private，owner获取容器内的对象列表
    public void testS3BucketPrivateOwnerList(){
        //创建bucket
        String s = "tdk-test-private-owner-list";
        conn.createBucket(s);
        //owner 获取容器内的对象。
        conn.listObjects(s);
        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }
    @Test
    //容器权限设置为private，owner上传对象
    public void testS3BucketPrivateOwnerPutObj(){
        //创建bucket
        String s = "tdk-test-private-owner-list";
        conn.createBucket(s);

        String key = "test/s3cmd2";
        File f = new File("D:\\download\\s3-tests-master.zip");
        PutObjectRequest request = new PutObjectRequest(s,key,f);
        conn.putObject(request);

        int i;
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName(s);
        ObjectListing objects = conn.listObjects(request1);

        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }

        conn.deleteObject(s,key);

        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }
    @Test
    //容器权限设置为private，owner删除对象
    public void testS3BucketPrivateOwnerDelObj(){
        //创建bucket
        String s = "tdk-test-private-owner-list";
        conn.createBucket(s);

        String key = "test/s3cmd2";
        File f = new File("D:\\download\\s3-tests-master.zip");
        PutObjectRequest request = new PutObjectRequest(s,key,f);
        conn.putObject(request);

        conn.deleteObject(s,key);
        int i;
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName(s);
        ObjectListing objects = conn.listObjects(request1);

        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }

        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }
    @Test
    //容器权限设置为private，owner覆盖对象
    public void testS3BucketPrivateOwnerOverWrite(){
        //创建bucket
        String s = "tdk-test-private-owner-over-write";
        conn.createBucket(s);

        String key = "test/s3cmd2";
        File f = new File("D:\\download\\s3-tests-master.zip");
        PutObjectRequest request = new PutObjectRequest(s,key,f);
        conn.putObject(request);
        conn.putObject(request);

        int i;
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName(s);
        ObjectListing objects = conn.listObjects(request1);

        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteObject(s,key);
        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }

    @Test
    //容器权限设置为private，owner获取容器的acl
    public void testS3BucketOwnerGetAcl(){

        String s = "tdk-test-private-owner-read-acl";
        CreateBucketRequest request = new CreateBucketRequest(s);
        Bucket bucket = conn.createBucket(request);

        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }

    @Test
    //容器权限设置为private，owner修改容器的acl
    public void testS3BucketPrivateOwnerWriteAcl(){
        String s = "tdk-test-private-owner-modify-acl";
        Bucket bucket = conn.createBucket(s);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);
        CanonicalGrantee grantee = new CanonicalGrantee("admin");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request1);

        CanonicalGrantee grantee1 = new CanonicalGrantee("tdk");
        list.grantPermission(grantee1,Permission.Read);
        SetBucketAclRequest request2 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request2);

        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }
    @Test
    //容器权限设置为private，用户权限设置为read，获取容器内的对象列表
    public void testS3BucketPrivateUserGetObjectList(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request1);

        CanonicalGrantee grantee = new CanonicalGrantee("tdk");
        list.grantPermission(grantee,Permission.Read);
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;

        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        ObjectListing objects = conn1.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为private，用户权限设置为write，上传对象
    public void testS3BucketPrivateUserUpLoadObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request1);

        CanonicalGrantee grantee = new CanonicalGrantee("tdk");
        list.grantPermission(grantee,Permission.Write);
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;

        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn1.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为write，删除对象
    public void testS3BucketPrivateUserDelObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request1);

        CanonicalGrantee grantee = new CanonicalGrantee("tdk");
        list.grantPermission(grantee,Permission.Write);
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;

        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn1.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为private，用户权限设置为write，覆盖对象
    public void testS3BucketPrivateUserOverWrite(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request1);

        CanonicalGrantee grantee = new CanonicalGrantee("tdk");
        list.grantPermission(grantee,Permission.Write);
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;

        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        conn1.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为read_acp，获取acl
    public void testS3BucketPrivateUserGetAcl(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request1);

        CanonicalGrantee grantee = new CanonicalGrantee("tdk");
        list.grantPermission(grantee,Permission.ReadAcp);
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

       System.out.print(conn1.getBucketAcl(bucketName).getGrantsAsList());
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为write_acp，修改acl
    public void testS3BucketPrivateUserModifyAcl(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        CanonicalGrantee grantee = new CanonicalGrantee("tdk");
        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        list.grantPermission(grantee,Permission.WriteAcp);
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        conn1.setBucketAcl(request);

        System.out.print(conn.getBucketAcl(bucketName).getGrantsAsList());
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为FULL CONTROL，获取容器内的对象列表
    public void testS3BucketPrivateUserFullControlGetObjectList(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        CanonicalGrantee grantee = new CanonicalGrantee("tdk");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;

        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        ObjectListing objects = conn1.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为FULL CONTROL，上传对象
    public void testS3BucketPrivateUserFullControlUpLoadObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        CanonicalGrantee grantee = new CanonicalGrantee("tdk");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn1.putObject(request2);

        ObjectListing objects = conn1.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为FULL CONTROL，删除对象
    public void testS3BucketPrivateUserFullControlDelObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        CanonicalGrantee grantee = new CanonicalGrantee("tdk");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn1.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为FULL CONTROL，覆盖对象
    public void testS3BucketPrivateUserFullControlOverWriteObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        CanonicalGrantee grantee = new CanonicalGrantee("tdk");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        conn1.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为FULL CONTROL，获取acl
    public void testS3BucketPrivateUserFullControlGetAcl(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        CanonicalGrantee grantee = new CanonicalGrantee("tdk");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        System.out.print(conn1.getBucketAcl(bucketName).getGrantsAsList());

        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为FULL CONTROL，修改acl
    public void testS3BucketPrivateUserFullControlModifyAcl(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        CanonicalGrantee grantee = new CanonicalGrantee("tdk");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request = new SetBucketAclRequest(bucketName,list);
        conn.setBucketAcl(request);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        conn1.setBucketAcl(request);

        System.out.print(conn.getBucketAcl(bucketName).getGrantsAsList());
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，匿名用户获取对象列表
    public void testS3BucketPrivateAnonymousGetObjectList(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);


        AmazonS3 conn1 = new AmazonS3Client();
        conn1.setEndpoint("http://10.254.3.68:7480");
        ObjectListing objects = conn1.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }

        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，匿名用户上传对象
    public void testS3BucketPrivateAnonymousUpLoadObjectList(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

       AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        AmazonS3 conn1 = new AmazonS3Client();
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn1.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }

        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为private，匿名用户删除对象
    public void testS3BucketPrivateAnonymousDelObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);


        AmazonS3 conn1 = new AmazonS3Client();
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }

        conn1.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为private，匿名用户覆盖对象
    public void testS3BucketPrivateAnonymousOverWriteObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        AmazonS3   conn1 = new AmazonS3Client();
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);
        conn1.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }

        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，匿名用户获取acl
    public void testS3BucketPrivateAnonymousGetAcl(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        AmazonS3  conn1 = new AmazonS3Client();
        conn1.setEndpoint("http://10.254.3.68:7480");

        System.out.print(conn1.getBucketAcl(bucketName).getGrantsAsList());

        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，匿名用户修改acl
    public void testS3BucketPrivateAnonymousModifyAcl(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        AmazonS3 conn1 = new AmazonS3Client();
        conn1.setEndpoint("http://10.254.3.68:7480");

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(bucketName,list);
        conn1.setBucketAcl(request1);

        System.out.print(conn.getBucketAcl(bucketName).getGrantsAsList());

        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限未设置read，获取容器内的对象列表
    public void testS3BucketPrivateNoGranteeGetObjectList(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        ObjectListing objects = conn1.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限未设置write，创建对象
    public void testS3BucketPrivateNoGranteeUploadObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn1.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
//        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限未设置write，删除对象
    public void testS3BucketPrivateNoGranteeDelObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn1.deleteObject(bucketName,objName);
        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限未设置write，覆盖对象
    public void testS3BucketPrivateNoGranteeOverWriteObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);
        conn1.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为private，用户权限未设置read_acp，获取acl
    public void testS3BucketPrivateNoGranteeGetAcl(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        System.out.print(conn1.getBucketAcl(bucketName).getGrantsAsList());
        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为private，用户权限未设置write_acp，修改acl
    public void testS3BucketPrivateNoGranteeWriteAcl(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "tdk";
        String sKey = "tdk";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(bucketName,list);
        conn1.setBucketAcl(request1);

        System.out.print(conn.getBucketAcl(bucketName).getGrantsAsList());
        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为private，用户权限设置read，key字段错误，获取容器内的对象列表
    public void testS3BucketPrivateErrKeyGetObjectList(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "err";
        String sKey = "err";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        ObjectListing objects = conn1.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }

        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为private，用户权限设置为write，key字段错误，上传对象
    public void testS3BucketPrivateErrKeyUploadObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "err";
        String sKey = "err";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn1.putObject(request2);

        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为write，key字段错误，删除对象
    public void testS3BucketPrivateErrKeyDelObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "err";
        String sKey = "err";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }

        conn1.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为private，用户权限设置为write，key字段错误，覆盖对象
    public void testS3BucketPrivateErrKeyOverWriteObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "err";
        String sKey = "err";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);
        conn1.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为private，用户权限设置为read_acp，key字段错误，获取acl
    public void testS3BucketPrivateErrKeyGetAcl(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "eer";
        String sKey = "eer";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        System.out.print(conn1.getBucketAcl(bucketName).getGrantsAsList());

        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为private，用户权限设置为write_acp，key字段错误，修改acl
    public void testS3BucketPrivateErrKeyModifyAcl() {
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin", "admin");
        list.setOwner(owner);

        String aKey = "err";
        String sKey = "err";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1, opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1, Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(bucketName, list);
        conn1.setBucketAcl(request1);

        System.out.print(conn.getBucketAcl(bucketName).getGrantsAsList());

        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为private，用户权限设置read，key字段为空，获取容器内的对象列表
    public void testS3BucketPrivateNullKeyGetObjectList(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "";
        String sKey = "";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        ObjectListing objects = conn1.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }

        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为write，key字段为空，创建对象
    public void testS3BucketPrivateNullKeyUploadObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "";
        String sKey = "";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn1.putObject(request2);

        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为write，key字段为空，删除对象
    public void testS3BucketPrivateNullKeyDelObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "";
        String sKey = "";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }

        conn1.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为write，key字段为空，覆盖对象
    public void testS3BucketPrivateNullKeyOverWriteObject(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "";
        String sKey = "";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);
        conn1.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为read_acp，key字段为空，获取acl
    public void testS3BucketPrivateNullKeyGetAcl(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "";
        String sKey = "";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        System.out.print(conn1.getBucketAcl(bucketName).getGrantsAsList());

        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为private，用户权限设置为write_acp，key字段为空，修改acl
    public void testS3BucketPrivateNullKeyModifyAcl(){
        String bucketName = "tdk-bucket-private-1";
        conn.createBucket(bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);

        String aKey = "";
        String sKey = "";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn1;
        conn1 = new AmazonS3Client(credentials1,opts1);
        conn1.setEndpoint("http://10.254.3.68:7480");

        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(bucketName,list);
        conn1.setBucketAcl(request1);

        System.out.print(conn.getBucketAcl(bucketName).getGrantsAsList());

        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为public-read，匿名用户获取obj列表
    public void testS3BucketPublicReadAnonymousGetObjectList(){
        String bucketName = "tdk-bucket-public-read-1";

        CreateBucketRequest request = new CreateBucketRequest(bucketName);
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        Bucket bucket = conn.createBucket(request);
        AccessControlList acl = conn.getBucketAcl(bucketName);

        AmazonS3 conn1 = new AmazonS3Client();
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        ObjectListing objects = conn1.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为public-read，匿名用户上传obj
    public void testS3BucketPublicReadAnonymousUploadObject(){
        String bucketName = "tdk-bucket-public-read-1";
        CreateBucketRequest request = new CreateBucketRequest(bucketName);
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        Bucket bucket = conn.createBucket(request);
        AccessControlList acl = conn.getBucketAcl(bucketName);

        AmazonS3 conn1 = new AmazonS3Client();
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn1.putObject(request2);

        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为public-read，匿名用户获取acl
    public void testS3BucketPublicReadAnonymousGetAcl(){
        String bucketName = "tdk-bucket-public-read-1";
        CreateBucketRequest request = new CreateBucketRequest(bucketName);
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        Bucket bucket = conn.createBucket(request);
        AccessControlList acl = conn.getBucketAcl(bucketName);

        AmazonS3 conn1 = new AmazonS3Client();
        conn1.setEndpoint("http://10.254.3.68:7480");

        System.out.print(conn1.getBucketAcl(bucketName).getGrantsAsList());
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为public-read，匿名用户修改acl
    public void testS3BucketPublicReadAnonymousModifyAcl(){
        String bucketName = "tdk-bucket-public-read-1";
        CreateBucketRequest request = new CreateBucketRequest(bucketName);
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        Bucket bucket = conn.createBucket(request);
        AccessControlList acl = conn.getBucketAcl(bucketName);

        AmazonS3 conn1 = new AmazonS3Client();
        conn1.setEndpoint("http://10.254.3.68:7480");

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);
        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(bucketName,list);
        conn1.setBucketAcl(request1);

        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为public-read-write，匿名用户获取obj列表
    public void testS3BucketPublicReadWriteAnonymousGetObjectList(){
        String bucketName = "tdk-bucket-public-read-1";

        CreateBucketRequest request = new CreateBucketRequest(bucketName);
        request.setCannedAcl(CannedAccessControlList.PublicReadWrite);
        Bucket bucket = conn.createBucket(request);
        AccessControlList acl = conn.getBucketAcl(bucketName);

        AmazonS3 conn1 = new AmazonS3Client();
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn.putObject(request2);

        ObjectListing objects = conn1.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }

    @Test
    //容器权限设置为public-read-write，匿名用户上传obj
    public void testS3BucketPublicReadWriteAnonymousUploadObject(){
        String bucketName = "tdk-bucket-public-read-1";

        CreateBucketRequest request = new CreateBucketRequest(bucketName);
        request.setCannedAcl(CannedAccessControlList.PublicReadWrite);
        Bucket bucket = conn.createBucket(request);
        AccessControlList acl = conn.getBucketAcl(bucketName);

        AmazonS3 conn1 = new AmazonS3Client();
        conn1.setEndpoint("http://10.254.3.68:7480");

        String objName= "test/s3cmd2";
        PutObjectRequest request2 = new PutObjectRequest(bucketName,objName,new File("D:\\download\\s3-tests-master.zip"));
        conn1.putObject(request2);

        ObjectListing objects = conn.listObjects(bucketName);
        int i;
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteObject(bucketName,objName);
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为public-read-write，匿名用户获取acl
    public void testS3BucketPublicReadWriteAnonymousGetAcl(){
        String bucketName = "tdk-bucket-public-read-write-1";
        CreateBucketRequest request = new CreateBucketRequest(bucketName);
        request.setCannedAcl(CannedAccessControlList.PublicReadWrite);
        Bucket bucket = conn.createBucket(request);
        AccessControlList acl = conn.getBucketAcl(bucketName);

        AmazonS3 conn1 = new AmazonS3Client();
        conn1.setEndpoint("http://10.254.3.68:7480");

        System.out.print(conn1.getBucketAcl(bucketName).getGrantsAsList());
        conn.deleteBucket(bucketName);
    }
    @Test
    //容器权限设置为public-read-write，匿名用户修改acl
    public void testS3BucketPublicReadWriteAnonymousModifyAcl(){
        String bucketName = "tdk-bucket-public-read-write-1";
        CreateBucketRequest request = new CreateBucketRequest(bucketName);
        request.setCannedAcl(CannedAccessControlList.PublicReadWrite);
        Bucket bucket = conn.createBucket(request);
        AccessControlList acl = conn.getBucketAcl(bucketName);

        AmazonS3 conn1 = new AmazonS3Client();
        conn1.setEndpoint("http://10.254.3.68:7480");

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);
        CanonicalGrantee grantee1 = new CanonicalGrantee("admin");
        list.grantPermission(grantee1,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(bucketName,list);
        conn1.setBucketAcl(request1);

        conn.deleteBucket(bucketName);
    }
    @Test
    //开启多版本
    public void testSetBucketVersioningEnable() {
        String bucketName = "tdk-bucket-version";
        conn.createBucket(bucketName);
        BucketVersioningConfiguration versioningConfiguration = new BucketVersioningConfiguration();
        versioningConfiguration.setStatus(BucketVersioningConfiguration.ENABLED);
        SetBucketVersioningConfigurationRequest request = new SetBucketVersioningConfigurationRequest(bucketName,versioningConfiguration);
        conn.setBucketVersioningConfiguration(request);

        BucketVersioningConfiguration configuration = conn.getBucketVersioningConfiguration(bucketName);
        System.out.print(configuration.getStatus());

    }

    @Test
    //暂停多版本
    public void testSetBucketVersioningSuspend() {
        String bucketName = "tdk-bucket-version";
        BucketVersioningConfiguration versioningConfiguration = new BucketVersioningConfiguration();
        versioningConfiguration.setStatus(BucketVersioningConfiguration.SUSPENDED);
        SetBucketVersioningConfigurationRequest request = new SetBucketVersioningConfigurationRequest(bucketName,versioningConfiguration);
        conn.setBucketVersioningConfiguration(request);

        BucketVersioningConfiguration configuration = conn.getBucketVersioningConfiguration(bucketName);
        System.out.print(configuration.getStatus());
    }

    @Test
    //上传一个在用户配额大小范围内的对象
    public void testS3ObjectUploadInQuota() {
        //创建容器zlj-bucket1
        CreateBucketRequest request_create_bucket = new CreateBucketRequest("zlj-bucket1");
        Bucket bucket = conn.createBucket(request_create_bucket);
        //向zlj-bucket1中上传一个在用户配额范围大小内的对象
        PutObjectRequest request_put_object = new PutObjectRequest("zlj-bucket1", "object01.txt", new File("D:\\object01.txt"));
        PutObjectResult result = conn.putObject(request_put_object);
    }
    @Test
    //上传一个超过用户配额大小的对象
    public void testS3ObjectUploadOverQuota() {
        //创建容器zlj-bucket1
        CreateBucketRequest request_create_bucket = new CreateBucketRequest("zlj-bucket1");
        Bucket bucket = conn.createBucket(request_create_bucket);
        //向zlj-bucket1中上传一个在用户配额范围大小内的对象
        PutObjectRequest request_put_object = new PutObjectRequest("zlj-bucket1", "object01.txt", new File("D:\\object01.txt"));
        PutObjectResult result = conn.putObject(request_put_object);
    }

    //tt----------------------------------------------------------------------------------------------------------------------
    @Test
    public void testGenerateObjectDownloadUrls1() {
        GeneratePresignedUrlRequest request  = new GeneratePresignedUrlRequest("tdk-bucket-1", "s3cmd5");
        request.setExpiration(null);
        URL url = conn.generatePresignedUrl(request);
        Assert.assertNotNull(url);
        System.out.println(url);
    }
    @Test
    //获取bucket 列表
    public void testS3BucketCreate1(){

        int i ;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0 ;i<bucketList.size();i++)
        {
            System.out.print(bucketList.get(i).getName() + "\n");
        }
    }
    @Test
    //获取bucket中的obj列表
    public void testS3BucketListObjectMaxGe1() {
        int i;
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName("tdk-bucket-1");
        ObjectListing objects = conn.listObjects(request1);

        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
    }
    @Test
    public void testGetObjectAcl1() {
        GetObjectAclRequest request = new GetObjectAclRequest("tdk-bucket-1", "s3cmd5");
        AccessControlList acl = conn.getObjectAcl(request);
        System.out.print(acl);
    }
    @Test
    public void testSetObjectAcl1() {
        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);
        CanonicalGrantee grantee = new CanonicalGrantee("testuser");
        grantee.setDisplayName("First User");
        list.grantPermission(grantee,Permission.Write);
        conn.setObjectAcl("zsw-test-1", "python1.chm", "RXhjatBHxasbPypMqf6S0XnLktdzZZu",list);
        conn.setObjectAcl("zsw-test-1", "python1.chm",CannedAccessControlList.PublicRead);
    }


    @Test
    public void testListBuckets() {
        List<Bucket> bucketList = conn.listBuckets();
        Assert.assertEquals(bucketList.get(0).getName(),"feihg");
        Assert.assertNotNull(bucketList);
        LOG.info(bucketList.size());

    }

    @Test
    public void testCreateBucket() {
        CreateBucketRequest request = new CreateBucketRequest("zsw-test-1");
        request.setCannedAcl(CannedAccessControlList.PublicRead);
        request.setRegion("CN");
        Bucket bucket = conn.createBucket(request);
        Assert.assertNotNull(bucket);
        LOG.info(bucket.toString());
    }

    @Test
    public void testDeleteBucket() {
        CreateBucketRequest request = new CreateBucketRequest("zsw-test");
        conn.createBucket(request);
        conn.deleteBucket("zsw-test");
    }

    @Test
    public void testGetBucketLocation() {
        String location = conn.getBucketLocation("zsw-test-1");
        LOG.info(location);
    }

    @Test
    public void testGetBucketAcl() {
        AccessControlList acl = conn.getBucketAcl("zsw-test-1");
        LOG.info(acl.toString());
    }

    @Test
    public void testPutBucketAcl() {
        conn.setBucketAcl("zsw-test-1", CannedAccessControlList.PublicReadWrite);
    }

    @Test
    public void testPutBucketAcl2() {
        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);
        CanonicalGrantee grantee = new CanonicalGrantee("testuser");
        grantee.setDisplayName("First User");
        list.grantPermission(grantee,Permission.Write);
        SetBucketAclRequest request = new SetBucketAclRequest("zsw-test-1",list);
        conn.setBucketAcl(request);
    }

    @Test
    public void testListBucketMultipartUploads() {
        MultipartUploadListing listing = conn.listMultipartUploads(new ListMultipartUploadsRequest("zsw-test-1"));
        LOG.info(listing.toString());
    }

    @Test
    public void testListMultiParts() {
        ListPartsRequest request = new ListPartsRequest("zsw-test-1","multi.txt","2~vPF_hjhgJdMvxkKGnwYGqOU2UR-qfTj");
        request.setEncodingType("Base64");
        conn.listParts(request);
    }

    @Test
    public void testSetBucketVersioning() {
        BucketVersioningConfiguration versioningConfiguration = new BucketVersioningConfiguration();
        versioningConfiguration.setStatus(BucketVersioningConfiguration.ENABLED);
        SetBucketVersioningConfigurationRequest request = new SetBucketVersioningConfigurationRequest("zsw-test-1",
                versioningConfiguration);
        conn.setBucketVersioningConfiguration(request);
    }

    @Test
    public void testInitialMultipart(){
        InitiateMultipartUploadResult result = conn.initiateMultipartUpload(new InitiateMultipartUploadRequest("zsw-test-1","test1"));
        LOG.info(result.getUploadId());

    }

    @Test
    public void restCompleteMultipart() {
        CompleteMultipartUploadRequest request = new CompleteMultipartUploadRequest();
        request.setBucketName("zsw-test");
        request.setKey("test1");
        request.setUploadId("2~8IGXSUGwhdRcgrCqP4Ou97ZTLMai_Q-");
        conn.completeMultipartUpload(request);
    }

    @Test
    public void testAbortMultipartUpload() {
        conn.abortMultipartUpload(new AbortMultipartUploadRequest("zsw-test","fake","1"));
    }

    @Test
    public void testGetBucketVersioning() {
        BucketVersioningConfiguration configuration = conn.getBucketVersioningConfiguration("op-test-bucket11");
        LOG.info(configuration.toString());
    }

    @Test
    public void testListObjects() {
        ListObjectsRequest request = new ListObjectsRequest();
        request.setBucketName("zsw-test-1");
        request.setEncodingType("utf-8");
        ObjectListing objects = conn.listObjects(request);
        Assert.assertNotNull(objects);
        LOG.info(objects.toString());
    }

    //Not support
    @Test
    public void testListVersions() {
        VersionListing listing = conn.listVersions("zsw-test-1","");
        LOG.info(listing.toString());
    }

    @Test
    public void testPutObject() {
        //PutObjectRequest request = new PutObjectRequest("zsw-test-1", "bc-onest-1.rar", new File("F:\\Download\\bc-onest.rar"));
        PutObjectRequest request = new PutObjectRequest("zsw-test-1", "python1.chm", new File("F:\\python.chm"));
        //request.setCannedAcl(CannedAccessControlList.PublicRead);
        PutObjectResult result = conn.putObject(request);
        Assert.assertNotNull(result);
        LOG.info(result.toString());
    }


    @Test
    public void testGetObject() {
        //S3Object s3Object = conn.getObject("zsw-test","python.chm");
        conn.getObject(new GetObjectRequest("zsw-test-1", "python.chm"),new File("F://1K.txt"));
    }

    @Test
    public void testGetObjectVersion() {
        GetObjectRequest request = new GetObjectRequest("zsw-test-1","python.chm");
        request.setVersionId("aWSHYx4HujWGA5JTH66d0H3GEhFnoil");
        S3Object s3Object= conn.getObject(request);
        LOG.info("Get Object Version ok");
    }


    @Test
    public void testDeleteObject() {
        conn.deleteObject("zsw-test-1", "python.chm");
    }

    @Test
    public void testDeleteVersions() {
        DeleteVersionRequest request = new DeleteVersionRequest("zsw-test-1", "python.chm","9WRM0jCaxq1sl4yw0WFYgdfC2NMzCZR");
        conn.deleteVersion(request);
    }

    @Test
    public void testGenerateObjectDownloadUrls() {
        GeneratePresignedUrlRequest request  = new GeneratePresignedUrlRequest("zsw-test-1", "python.chm");
        request.setExpiration(null);
        URL url = conn.generatePresignedUrl(request);
        Assert.assertNotNull(url);
        System.out.println(url);
    }

    @Test
    public void testCopyObject() {
        CopyObjectRequest request = new CopyObjectRequest("zsw-test-1","python.chm","zsw-test-1","python_copy.chm");
        request.setSourceVersionId("InpMZ4hP4GNuawGabZZFcpfIZ55dIn7");
        CopyObjectResult result = conn.copyObject(request);
        LOG.info(result.toString());
    }

    @Test
    public void testGetObjectInfo() {
        GetObjectMetadataRequest request = new GetObjectMetadataRequest("zsw-test-1", "python.chm");
        ObjectMetadata metadata = conn.getObjectMetadata(request);
        LOG.info(metadata.toString());
    }

    @Test
    public void testGetObjectVersionInfo() {
        GetObjectMetadataRequest request = new GetObjectMetadataRequest("zsw-test-1", "python.chm");
        request.setVersionId("InpMZ4hP4GNuawGabZZFcpfIZ55dIn7");
        ObjectMetadata metadata = conn.getObjectMetadata(request);
        LOG.info(metadata.toString());
    }

    @Test
    public void testGetObjectAcl() {
        GetObjectAclRequest request = new GetObjectAclRequest("zsw-test-1", "python1.chm");
        AccessControlList acl = conn.getObjectAcl(request);
        LOG.info(acl.toString());
    }

    @Test
    public void testSetObjectAcl() {
        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("admin","admin");
        list.setOwner(owner);
        CanonicalGrantee grantee = new CanonicalGrantee("testuser");
        grantee.setDisplayName("First User");
        list.grantPermission(grantee,Permission.Write);
        conn.setObjectAcl("zsw-test-1", "python1.chm", "RXhjatBHxasbPypMqf6S0XnLktdzZZu",list);
    }

    @Test
    public void testInitiateMultipartUpload() {
        InitiateMultipartUploadRequest request = new InitiateMultipartUploadRequest("zsw-test-1", "multi.txt");
        InitiateMultipartUploadResult result = conn.initiateMultipartUpload(request);
        Assert.assertNotNull(result);
    }

    @Test
    public void testMultipartUpload() {

    }

    @Test
    public void testGetS3AccountOwner() {
        Owner owner = conn.getS3AccountOwner();
        LOG.info(owner.toString());
    }

    @Test
    public void testDoesBucketExist(){
        boolean result = conn.doesBucketExist("zsw-test-1");
        Assert.assertTrue(result);
    }

    @Test
    public void testHeadBucket(){
        HeadBucketRequest request = new HeadBucketRequest("zsw-test-1");
        HeadBucketResult result = conn.headBucket(request);
        Assert.assertNotNull(result);
    }

    @Test
    public void testDeleteObjects() {
        DeleteObjectsRequest req = new DeleteObjectsRequest("zsw-test-1");
        List<DeleteObjectsRequest.KeyVersion> list = new ArrayList<DeleteObjectsRequest.KeyVersion>();
        DeleteObjectsRequest.KeyVersion key1 = new DeleteObjectsRequest.KeyVersion("python.chm","InpMZ4hP4GNuawGabZZFcpfIZ55dIn7");
        DeleteObjectsRequest.KeyVersion key2 = new DeleteObjectsRequest.KeyVersion("python.chm","aWSHYx4HujWGA5JTH66d0H3GEhFnoil");
        list.add(key1);
        list.add(key2);
        req.setKeys(list);
        DeleteObjectsResult result = conn.deleteObjects(req);
        LOG.info(result.toString());
    }

    //Not support
    @Test
    public void testGetBucketLoggingConfiguration() {
        BucketLoggingConfiguration conf = conn.getBucketLoggingConfiguration("zsw-test");
        LOG.info(conf.toString());
    }

    //Not support
    @Test
    public void testSetBucketLoggingConfiguration() {
        BucketLoggingConfiguration conf = new BucketLoggingConfiguration("zsw-test", "2016-");
        SetBucketLoggingConfigurationRequest req = new SetBucketLoggingConfigurationRequest("zsw-test", conf);
        conn.setBucketLoggingConfiguration(req);
    }


    //Not support
    @Test
    public void testGetBucketCrossOriginConf() {
        conn.getBucketCrossOriginConfiguration("zsw-test");
    }

    //Not support
    @Test
    public void testGetBucketTaggingConfiguration(){
        conn.getBucketTaggingConfiguration("zsw-test");
    }

    //Not support
    @Test
    public void testGetBucketNotificationConf() {
        conn.getBucketNotificationConfiguration("zsw-test");
    }

    //Not support
    @Test
    public void testGetBucketPolicy() {
        conn.getBucketPolicy("zsw-test-1");
    }

    //Not support
    @Test
    public void testGetBucketReplicationConfiguration(){
        conn.getBucketReplicationConfiguration("zsw-test");
    }

    @Test
    public void testDoesObjectExist() {
        boolean result = conn.doesObjectExist("zsw-test-1", "python.chm");
        Assert.assertTrue(result);
    }

    @Test
    public void testDeleteBuckets() {
        List<Bucket> bucketList = conn.listBuckets();
        for(Bucket bucket: bucketList) {
            if(bucket.getName().startsWith("s3test-")) {
                try {
                    ObjectListing listing = conn.listObjects(bucket.getName());
                    List<S3ObjectSummary> list = listing.getObjectSummaries();
                    for (S3ObjectSummary s3ObjectSummary : list) {
                        conn.deleteObject(bucket.getName(), s3ObjectSummary.getKey());
                    }
                    conn.deleteBucket(bucket.getName());
                }catch (Exception e) {
                    LOG.info(e.getMessage());
                }
            }
        }
    }

    */

}

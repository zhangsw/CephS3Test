//import com.amazonaws.ClientConfiguration;
//import com.amazonaws.auth.AWSCredentials;
//import com.amazonaws.auth.BasicAWSCredentials;
//import com.amazonaws.services.s3.AmazonS3;
//import com.amazonaws.services.s3.AmazonS3Client;
//import com.amazonaws.services.s3.model.*;
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.FileNotFoundException;
//import java.io.InputStream;
//import java.net.URL;
//import java.util.ArrayList;
//import java.util.LinkedList;
//import java.util.List;
//
//import java.io.File;
//import java.util.List;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.auth.AWSCredentials;
import com.amazonaws.auth.BasicAWSCredentials;
import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.AmazonS3Client;
import com.amazonaws.services.s3.internal.InputSubstream;
import com.amazonaws.services.s3.model.*;
import com.amazonaws.services.s3.transfer.model.UploadResult;
import org.apache.log4j.Logger;
//import org.junit.Assert;
//import org.junit.Before;
//import org.junit.Test;

import javax.swing.plaf.basic.BasicInternalFrameTitlePane;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.*;
import java.io.RandomAccessFile;

import java.lang.*;

/**
 * Created by pt on 2016/7/25.
 */
public class cephJavaS3 {

    public static void main(String[] args ) throws FileNotFoundException {

        String aKey = "FF6KMKI1HXG72BAM9JFF";
        String sKey = "IoWAj3iBIyvog8AhEh2cJ4iPjyvxZAdmBJbMNlZj";
        ClientConfiguration opts1 = new ClientConfiguration();
        AWSCredentials credentials1 = new BasicAWSCredentials(aKey, sKey);
        AmazonS3 conn;
        conn = new AmazonS3Client(credentials1,opts1);
        conn.setEndpoint("http://10.254.9.20:7480");

//        testS3MultiPartUpload(conn);
        test(conn);
    }

    //t------------------------------------------------------------------------------------------

    public static void test(AmazonS3 conn){
        String bucketName="tdk-bucket-src-1";
        String objName = "test/15M2";

        conn.createBucket(bucketName);
        System.out.print("--------list-bucket----------\n");
        testS3BucketList(conn);

//        System.out.print("--------put-objects----------\n");
//        testS3ObjectPut(conn,bucketName,objName);
//        System.out.print("--------list-object----------\n");
//        testS3ObjectList(conn,bucketName);

//        System.out.print("--------down-load-object----------\n");
//        GetObjectRequest request = new GetObjectRequest(bucketName,objName);
//        request.setRange(0,10);
//        conn.getObject(request,new File("D:\\test\\10.rar"));
    }

    public static void testS3MultiPartUpload(AmazonS3 conn) throws FileNotFoundException {
        String bucketName="tdk-test-b-1";
        String objName = "test/15M";
        System.out.print("--------create-bucket----------\n");
        testS3BucketCreate(conn,bucketName);

        System.out.print("--------multi upload initial----------\n");

        InitiateMultipartUploadRequest request_initiate_mutipart_upload = new InitiateMultipartUploadRequest(bucketName, objName);
        InitiateMultipartUploadResult result_initiate_mutipart_upload = conn.initiateMultipartUpload(request_initiate_mutipart_upload);
        System.out.println(result_initiate_mutipart_upload.getUploadId());

        //上传第1个分块，5M大小
        System.out.print("--------multi upload part1----------\n");
        UploadPartRequest request_upload_part_1 = new UploadPartRequest();
        InputStream in_part_1 = null;
        in_part_1 = new InputSubstream(new FileInputStream(new File("D:\\download\\test15")), 0, 5*1024*1024,true);
        request_upload_part_1.setBucketName(bucketName);
        request_upload_part_1.setKey(objName);
        request_upload_part_1.setUploadId(result_initiate_mutipart_upload.getUploadId());
        request_upload_part_1.setInputStream(in_part_1);
        request_upload_part_1.setPartSize(5*1024*1024);
        request_upload_part_1.setPartNumber(1);
        UploadPartResult result_upload_part_1 = conn.uploadPart(request_upload_part_1);

        //上传第2个分块，5M大小
        System.out.print("--------multi upload part2----------\n");
        UploadPartRequest request_upload_part_2 = new UploadPartRequest();
        InputStream in_part_2 = null;
        in_part_2 = new InputSubstream(new FileInputStream(new File("D:\\download\\test15")), 5*1024*1024, 5*1024*1024,true);
        request_upload_part_2.setBucketName(bucketName);
        request_upload_part_2.setKey(objName);
        request_upload_part_2.setUploadId(result_initiate_mutipart_upload.getUploadId());
        request_upload_part_2.setInputStream(in_part_2);
        request_upload_part_2.setPartSize(5*1024*1024);
        request_upload_part_2.setPartNumber(2);
        UploadPartResult result_upload_part_2 = conn.uploadPart(request_upload_part_2);

        //上传第3个分块，5M大小
        System.out.print("--------multi upload part3----------\n");
        UploadPartRequest request_upload_part_3 = new UploadPartRequest();
        InputStream in_part_3 = null;
        in_part_3 = new InputSubstream(new FileInputStream(new File("D:\\download\\test15")), 5*1024*1024*2, 5*1024*1024,true);
        request_upload_part_3.setBucketName(bucketName);
        request_upload_part_3.setKey(objName);
        request_upload_part_3.setUploadId(result_initiate_mutipart_upload.getUploadId());
        request_upload_part_3.setInputStream(in_part_3);
        request_upload_part_3.setPartSize(5*1024*1024);
        request_upload_part_3.setPartNumber(3);
        request_upload_part_3.setLastPart(true);
        UploadPartResult result_upload_part_3 = conn.uploadPart(request_upload_part_3);
        System.out.println(result_initiate_mutipart_upload.getUploadId());
        System.out.print("--------abortMultipartUpload----------\n");
        conn.abortMultipartUpload(new AbortMultipartUploadRequest(bucketName,objName,result_initiate_mutipart_upload.getUploadId()));
//如果abort，是将所有的上传的块都丢失。
//        MultipartUploadListing listing = conn.listMultipartUploads(new ListMultipartUploadsRequest(bucketName));
//        System.out.println("###############################################");
//        System.out.println(listing.getMultipartUploads().get(0));
        //完成一个muti-part请求
//         private static void completeMultipartUpload(AmazonS3 s3Client,String bucketName, String key, String uploadId, List<PartETag> eTags)
//        CompleteMultipartUploadRequest request_complete = new CompleteMultipartUploadRequest();
//        request_complete.setBucketName(bucketName);
//        request_complete.setKey(objName);
//        request_complete.setUploadId(result_initiate_mutipart_upload.getUploadId());
//        List<PartETag> list_etags = new LinkedList<PartETag>();
//        list_etags.add(result_upload_part_1.getPartETag());
//        list_etags.add(result_upload_part_2.getPartETag());
//        list_etags.add(result_upload_part_3.getPartETag());
//        request_complete.setPartETags(list_etags);
//        CompleteMultipartUploadResult result_complete = conn.completeMultipartUpload(request_complete);

        System.out.print("--------list-objects----------\n");
        testS3ObjectList(conn,bucketName);
    }

    public static void testS3ObjectAcl(AmazonS3 conn){
        String bucketName="tdk-test-2";
        String objName = "test/obj1";

        conn.createBucket(bucketName);

        System.out.print("--------put-objects----------\n");
        testS3ObjectPut(conn,bucketName,objName);

        System.out.print("--------list-object----------\n");
        testS3ObjectList(conn,bucketName);

        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("tdkf","tdkf");
        list.setOwner(owner);
        CanonicalGrantee grantee = new CanonicalGrantee("tdkf");
        CanonicalGrantee grantee1 = new CanonicalGrantee("tdk");
        list.grantPermission(grantee,Permission.FullControl);
        list.grantPermission(grantee1,Permission.WriteAcp);
        SetObjectAclRequest request = new SetObjectAclRequest(bucketName,objName,list);
        conn.setObjectAcl(request);

        AccessControlList acl = conn.getObjectAcl(bucketName,objName);
        System.out.print(acl.getGrantsAsList()+"\n");

        testS3ObjectDelete(conn,bucketName,objName);
        testS3BucketDelete(conn,bucketName);

    }

    public static void testS3ObjectAcl1(AmazonS3 conn){
        String bucketName="tdk-test-2";
        String objName = "test/obj1";

        conn.createBucket(bucketName);

        System.out.print("--------put-objects----------\n");
        testS3ObjectPut(conn,bucketName,objName);

        System.out.print("--------list-object----------\n");
        testS3ObjectList(conn,bucketName);

        conn.setObjectAcl(bucketName,objName,CannedAccessControlList.Private);
        AccessControlList acl = conn.getObjectAcl(bucketName,objName);
        System.out.print(acl.getGrantsAsList()+"\n");

        testS3ObjectDelete(conn,bucketName,objName);
        testS3BucketDelete(conn,bucketName);

    }

    public static void testS3Bucket1(AmazonS3 conn){
        String bucketName="tdk-test-1";
        String objName = "test/obj1";
        String objName1 = "5M";
        String objName2 = "test/obj3";

//        conn.createBucket(bucketName);
//        conn.deleteBucket(bucketName);
//        conn.deleteBucket("sssssssssssssssssssssssssssssssssssssssssssssssssssssssssssssss");

//        testSetBucketVersioningEnable(conn,bucketName);

//        System.out.print("--------list-object----------\n");
//        testVersioningListObj(conn,bucketName);

//        VersionListing listing = conn.listVersions(bucketName,"");
//        listing.getVersionSummaries().size();
//        int i ;
//        for (i=0;i< listing.getVersionSummaries().size();i++) {
//
//            System.out.print(listing.getVersionSummaries().get(i).getVersionId() + " ");
//            System.out.print(listing.getVersionSummaries().get(i).getKey() + "\n");
//            conn.deleteVersion(bucketName,listing.getVersionSummaries().get(i).getKey(),listing.getVersionSummaries().get(i).getVersionId());
//        }
//        System.out.print("--------list-object----------\n");
//        testVersioningListObj(conn,bucketName);
//        System.out.print("--------put-objects----------\n");
//        testS3ObjectPut(conn,bucketName,objName);
//        conn.deleteObject(bucketName,"100wks_64k150000");
//        System.out.print("--------del-object----------\n");
//        conn.deleteObject(bucketName,objName);

//        System.out.print("--------down-load-object----------\n");
//        GetObjectRequest request = new GetObjectRequest(bucketName,objName);
//        request.setRange(0,10);
//        conn.getObject(request,new File("D:\\test\\10.rar"));

        System.out.print("--------list-bucket----------\n");
        testS3BucketList(conn);
//        System.out.print("--------list-objects----------\n");
//        testS3ObjectList(conn,bucketName);
//
//        int i;
//        ObjectListing objects = conn.listObjects(bucketName);
//        for (i=0 ; i < objects.getObjectSummaries().size();i++){
//            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
//            testS3ObjectDelete(conn,bucketName,objects.getObjectSummaries().get(i).getKey());
//        }


//        Date date = new Date();
//        Calendar c = Calendar.getInstance();
//        c.setTime(date);
//        c.add(Calendar.MINUTE,3);
//        System.out.println(c.getTime());
//
//        GeneratePresignedUrlRequest request  = new GeneratePresignedUrlRequest(bucketName, objName);
//        request.setExpiration(c.getTime());
//        URL url = conn.generatePresignedUrl(request);
//        System.out.println(url);

//        int i;
//        ListObjectsRequest request1 = new ListObjectsRequest();
//        request1.setBucketName(bucketName);
//        request1.setPrefix("1test1");
//        request1.setMarker("test1/obj2");
//        request1.setMaxKeys(0);
//        request1.setDelimiter(".");
//        ObjectListing objects = conn.listObjects(request1);

//        for (i=0 ; i < objects.getObjectSummaries().size();i++){
//            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
//        }
//        conn.deleteObject(bucketName,objName);
//        System.out.print("--------list-objects----------\n");
//        testS3ObjectList(conn,bucketName);
//        ListObjectsRequest request = new ListObjectsRequest();
//        request.setBucketName(bucketName);
//        request.setMaxKeys(500000);
//        ObjectListing objects = conn.listObjects(request);
//        System.out.println(objects.getObjectSummaries().size());
//        conn.deleteObject(bucketName,"100wks_64k1287");
//        conn.deleteObject(bucketName,"100wks_64k1297");

//        int i;
//        for (i=0 ; i < objects.getObjectSummaries().size();i++){
//            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
//            conn.deleteObject(bucketName,objects.getObjectSummaries().get(i).getKey());

//        }

//
//        System.out.print("--------list-objects----------\n");
//        testS3ObjectList(conn,bucketName);
//        System.out.print("--------del-bucket----------\n");
//        conn.deleteBucket(bucketName);
//        System.out.print("--------list-bucket----------\n");
//        testS3BucketList(conn);


//        ObjectMetadata ob = new  conn.getObjectMetadata(bucketName,objName);
//        conn.getObjectMetadata(bucketName,objName).getContentLength();

//        DeleteObjectsRequest request = new DeleteObjectsRequest(bucketName);
//        DeleteObjectsRequest.KeyVersion key1 = new DeleteObjectsRequest.KeyVersion(objName);
//        DeleteObjectsRequest.KeyVersion key2= new DeleteObjectsRequest.KeyVersion(objName1);
//        DeleteObjectsRequest.KeyVersion key3= new DeleteObjectsRequest.KeyVersion(objName2);
//        List<DeleteObjectsRequest.KeyVersion> keyList = new ArrayList<DeleteObjectsRequest.KeyVersion>();
//        keyList.add(key1);
//        keyList.add(key2);
//        keyList.add(key3);
//        request.setKeys(keyList);
//        conn.deleteObjects(request);
//
//        conn.deleteObjects(request);
//        System.out.print("--------list-bucket----------\n");
//        testS3BucketList(conn);
//        System.out.print("--------list-objects----------\n");
//        testS3ObjectList(conn,bucketName);
//        testS3ObjectPut(conn,bucketName,objName);
//        testS3ObjectPut(conn,bucketName,objName1);
//        testS3ObjectPut(conn,bucketName,objName2);


//        System.out.print("--------list-bucket----------\n");
//        testS3BucketList(conn);
//        System.out.print("--------list-objects----------\n");
//        testS3ObjectList(conn,bucketName);
//        object.getContentLength();
//        GetObjectRequest request = new GetObjectRequest(bucketName,objName);
//        request.setRange(10);
//        request.setRange(11,22);
//        conn.createBucket(bucketName);
//        testS3ObjectPut(conn,bucketName,objName);
//        System.out.print("--------list-bucket----------\n");
//        testS3BucketList(conn);
//        System.out.print("--------list-objects----------\n");
//        testS3ObjectList(conn,bucketName);
//        System.out.print("--------get-objects----------\n");
//        GetObjectRequest request = new GetObjectRequest(bucketName,objName);
//        request.setRange(10);
//        request.setRange(11,22);
//        conn.getObject(request,new File("D:\\1.test"));
//        for (int n =0 ; n < 10 ; n++ ) {
//            String bucketName="tdk-contain-" + n;
////            conn.createBucket(bucketName);
//            conn.deleteBucket(bucketName);
//        }
//        conn.createBucket("tdk-contain-" +10);
//        System.out.print("--------create-bucket----------\n");
//        testS3BucketCreate(conn,bucketName);
//        conn.deleteObject(bucketName,objName);
//        conn.deleteBucket(bucketName);
//        System.out.print("--------list-objects----------\n");
//        testS3ObjectList(conn,bucketName);

//        System.out.print("--------put-objects----------\n");
//        testS3ObjectPut(conn,bucketName,objName);

//        System.out.print("--------list-objects----------\n");
//        testS3ObjectList(conn,bucketName);

//        System.out.print("--------del-object----------\n");
//        conn.deleteObject(bucketName,objName);

//        System.out.print("--------delete-bucket----------\n");
//        testS3BucketDelete(conn,bucketName);

//        System.out.print("--------list-bucket----------\n");
//        testS3BucketList(conn);
    }

    public  static void testS3CopyObject(AmazonS3 conn){
        String srcBucketName="tdk-bucket-src";
        String dstBucketName="tdk-bucket-dst";
        String srcObjName = "test/src";
        String dstObjName = "test/dst";

//        System.out.print("--------create-bucket----------\n");
//        conn.createBucket(srcBucketName);
//        conn.createBucket(dstBucketName);

        System.out.print("--------put-object----------\n");
        testS3ObjectPut(conn,srcBucketName,srcObjName);
//        File f = new File("/root/tongdekui/0b");
//        PutObjectRequest request = new PutObjectRequest(srcBucketName,srcObjName,f);
//        conn.putObject(request);
//
        System.out.print("--------copy-object----------\n");
        conn.copyObject(srcBucketName,srcObjName,srcBucketName,dstObjName);

        System.out.print("--------list-src-bucket-object----------\n");
        testS3ObjectList(conn,srcBucketName);

        System.out.print("--------list-src-bucket-object----------\n");
        testS3ObjectList(conn,dstBucketName);
//        conn.getObject(new GetObjectRequest(dstBucketName, dstObjName),new File("0B.copy"));
//        conn.deleteObject(srcBucketName,"test/dst");
//        conn.deleteObject(srcBucketName,"test/obj1");
//        conn.deleteObject(srcBucketName,"test/src");
//        conn.deleteBucket(srcBucketName);
//        conn.deleteObject(dstBucketName,"test/dst");
//        conn.deleteObject(dstBucketName,"test/dst0B");
//        conn.deleteBucket(dstBucketName);
    }

    public static void testS3BucketSetPrivate(AmazonS3 conn) {
        String s = "tdk-test-private";
        CreateBucketRequest request = new CreateBucketRequest(s);
        request.setCannedAcl(CannedAccessControlList.PublicReadWrite);
        Bucket bucket = conn.createBucket(request);
        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList()+"\n");
        conn.deleteBucket(s);
    }
    public static void testS3SetPublicUrl(AmazonS3 conn){
        String bucketName="tdk-bucket-src";
        String srcObjName = "test/obj1";
        testS3BucketList(conn);
        testS3ObjectList(conn,bucketName);
        conn.createBucket(bucketName);
        testS3ObjectPut(conn,bucketName,srcObjName);
        GeneratePresignedUrlRequest request  = new GeneratePresignedUrlRequest(bucketName, srcObjName);
        request.setExpiration(null);
        URL url = conn.generatePresignedUrl(request);
        System.out.println(url);
        conn.deleteObject(bucketName,srcObjName);
        conn.createBucket(bucketName);
    }


    public static void testS3ObjectList(AmazonS3 conn,String bucketName){
        int i;
        ObjectListing objects = conn.listObjects(bucketName);
        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }

    }
    public static void testS3ObjectPut(AmazonS3 conn,String bucketName,String objName){
//        File f = new File("D:\\download\\6g");
        File f = new File("D:\\download\\test15");
//        File f = new File("D:\\download\\0b.txt");
        PutObjectRequest request = new PutObjectRequest(bucketName,objName,f);
        conn.putObject(request);

    }


    public static void testS3Version(AmazonS3 conn){
        String bucketName="tdk-test-1";
        String objName = "version/obj1";

//        System.out.print("--------create-bucket----------\n");
//        conn.createBucket(bucketName);
//
//        System.out.print("--------enable-version----------\n");
//        testSetBucketVersioningEnable(conn,bucketName);
//
//        System.out.print("--------put-object----------\n");
//        testS3ObjectPut(conn,bucketName,objName);
//        testS3ObjectPut(conn,bucketName,objName);

//        System.out.print("--------down load-object----------\n");
//        GetObjectRequest request = new GetObjectRequest(bucketName,objName,"bceU4NkzfhLnoaGviAn6xLlONexoFeK");
//        conn.getObject(request,new File("D:\\test\\version"));
        System.out.print("--------delete-object----------\n");
        conn.deleteObject(bucketName,objName);
//        conn.deleteVersion(bucketName,objName,"4.DAqT1De-u6x79KvSPPY-z.NaPDdT4");
//        conn.deleteVersion(bucketName,objName,"67ubKzsqHZ5HC4UPIvIwyGzizqiF9ht");
//        conn.deleteVersion(bucketName,objName,"NqNn3QwjzTsu9M1ybp1AzbLxiKLrvYA");
//        conn.deleteVersion(bucketName,objName,"Ulu2QeUh2w.AoZ06clt1ST2Aeoqpqsk");
//        conn.deleteVersion(bucketName,objName,"XIpb4srGkH24gWQyWFc0dCfnOJ1csDI");
//        conn.deleteVersion(bucketName,objName,"elJIL-YFtRKVRk-6FGo4-gF.9HBHaiB");
//        conn.deleteObject(bucketName,objName);
        System.out.print("--------list-object----------\n");
        testVersioningListObj(conn,bucketName);
//        testS3ObjectList(conn,bucketName);

//        System.out.print("--------del-bucket----------\n");
//        testS3BucketDelete(conn,bucketName);

    }
    public static void testS3GetObjVersion(AmazonS3 conn){

    }

    public static void testVersioningListObj(AmazonS3 conn,String bucketName) {

        VersionListing listing = conn.listVersions(bucketName,"");
        listing.getVersionSummaries().size();
        int i ;
        for (i=0;i< listing.getVersionSummaries().size();i++){
            System.out.print(listing.getVersionSummaries().get(i).getVersionId()+" ");
            System.out.print(listing.getVersionSummaries().get(i).getKey()+"\n");
        }

    }


    public static void testSetBucketVersioningEnable(AmazonS3 conn ,String bucketName) {
//        String bucketName = "tdk-bucket-version";
//        conn.createBucket(bucketName);
        BucketVersioningConfiguration versioningConfiguration = new BucketVersioningConfiguration();
        versioningConfiguration.setStatus(BucketVersioningConfiguration.ENABLED);
        SetBucketVersioningConfigurationRequest request = new SetBucketVersioningConfigurationRequest(bucketName,versioningConfiguration);
        conn.setBucketVersioningConfiguration(request);

        BucketVersioningConfiguration configuration = conn.getBucketVersioningConfiguration(bucketName);
        System.out.print(configuration.getStatus()+"\n");



    }

    public static void testS3GetObject(AmazonS3 conn){
        String bucketName="tdk-bucket-test-1";
        String objName = "test/0B";
        System.out.print("--------create-bucket----------\n");
        testS3BucketCreate(conn,bucketName);
        System.out.print("--------put-object----------\n");
        File f = new File("/root/tongdekui/0b");
        PutObjectRequest request = new PutObjectRequest(bucketName,objName,f);
        conn.putObject(request);
        System.out.print("--------list-objects----------\n");
        testS3ObjectList(conn,bucketName);
        System.out.print("--------down-load-object----------\n");
        conn.getObject(new GetObjectRequest(bucketName, objName),new File("0B.txt"));
        testS3ObjectDelete(conn,bucketName,objName);
        testS3BucketDelete(conn,bucketName);

    }

    public static void testS3Bucket3(AmazonS3 conn){
        String bucketName="tdk-bucket-test-1";
        String objName = "test/15M";
        String objName1 = "test/6g";


//        System.out.print("--------create-bucket----------\n");
//        testS3BucketCreate(conn,bucketName);
//        testS3BucketDelete(conn,bucketName);

//        System.out.print("--------put-objects----------\n");
//        testS3ObjectPut(conn,bucketName,objName);


//        File f = new File("/root/tongdekui/0b");
//        PutObjectRequest request = new PutObjectRequest(bucketName,objName,f);
//        conn.putObject(request);

        System.out.print("--------list-objects----------\n");
        testS3ObjectList(conn,bucketName);
        testS3ObjectDelete(conn,bucketName,objName);
        testS3BucketDelete(conn,bucketName);

    }

    public static void testS3Bucket2(AmazonS3 conn){
        String bucketName="tdk-bucket-test-1";
        String objName = "test/dir";
        String objName1 = "test/6g";


//        System.out.print("--------create-bucket----------\n");
//        testS3BucketCreate(conn,bucketName);
//        testS3BucketDelete(conn,bucketName);

//        System.out.print("--------put-objects----------\n");
//        testS3ObjectPut(conn,bucketName,objName);

//        File f = new File("/root/tongdekui/0b");
//        PutObjectRequest request = new PutObjectRequest(bucketName,objName,f);
//        conn.putObject(request);

        System.out.print("--------list-objects----------\n");
        testS3ObjectList(conn,bucketName);
        testS3ObjectDelete(conn,bucketName,objName1);
        testS3BucketDelete(conn,bucketName);

    }



    public static void testS3BucketUserDel(AmazonS3 conn){
        String s = "tdk-test-use-del";
        CreateBucketRequest request = new CreateBucketRequest(s);
        Bucket bucket = conn.createBucket(request);
        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("tdktest","tdktest");
        list.setOwner(owner);
        CanonicalGrantee grantee = new CanonicalGrantee("tdktest");
        list.grantPermission(grantee,Permission.FullControl);
        CanonicalGrantee grantee1 = new CanonicalGrantee("tdk");
        list.grantPermission(grantee1,Permission.Write);
        SetBucketAclRequest request1 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request1);

        AccessControlList list1 = new AccessControlList();
        Owner owner1 = new Owner("tdktest","tdktest");
        list1.setOwner(owner1);
        CanonicalGrantee grantee3 = new CanonicalGrantee("tdktest");
        list1.grantPermission(grantee3,Permission.FullControl);
        SetBucketAclRequest request3 = new SetBucketAclRequest(s,list1);
        conn.setBucketAcl(request3);

        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList());
        conn.deleteBucket(s);
    }
    public static void testS3BucketCreateUserRead(AmazonS3 conn){
        String s = "tdk-test-read-user";
        CreateBucketRequest request = new CreateBucketRequest(s);
        Bucket bucket = conn.createBucket(request);
        AccessControlList list = new AccessControlList();
        Owner owner = new Owner("tdktest","tdktest");
        list.setOwner(owner);
        CanonicalGrantee grantee = new CanonicalGrantee("tdktest");
        list.grantPermission(grantee,Permission.FullControl);
        SetBucketAclRequest request1 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request1);

        CanonicalGrantee grantee1 = new CanonicalGrantee("tdk1");
        list.grantPermission(grantee1,Permission.WriteAcp);
        SetBucketAclRequest request2 = new SetBucketAclRequest(s,list);
        conn.setBucketAcl(request2);

        AccessControlList acl = conn.getBucketAcl(s);
        System.out.print(acl.getGrantsAsList()+"\n");
        conn.deleteBucket(s);

    }



    public static void testS3ObjectList(AmazonS3 conn){
        String bucketName="tdk-bucket-test-1";
        conn.createBucket(bucketName);
        String objName1 = "obj1";
        String objName2 = "test/obj2";
        String objName3 = "test/obj3";
        testS3ObjectPut(conn,bucketName,objName1);
        testS3ObjectPut(conn,bucketName,objName2);
        testS3ObjectPut(conn,bucketName,objName3);

        int i;
        ListObjectsRequest request1 = new ListObjectsRequest();
        request1.setBucketName(bucketName);
//        request1.setPrefix("1test");
//        request1.setMarker("test/obj2");
//        request1.setMaxKeys(0);
        request1.setDelimiter("/");
        ObjectListing objects = conn.listObjects(request1);

        for (i=0 ; i < objects.getObjectSummaries().size();i++){
            System.out.print(objects.getObjectSummaries().get(i).getKey()+"\n");
        }
        conn.deleteObject(bucketName,objName1);
        conn.deleteObject(bucketName,objName2);
        conn.deleteObject(bucketName,objName3);
        conn.deleteBucket(bucketName);
    }

    public static void testS3BucketDelNo(AmazonS3 conn){
        String bucketName="tdk-bucket-test-1";
        conn.deleteBucket(bucketName);
    }

    public static void testS3BucketSameName(AmazonS3 conn){
        String bucketName="tdk-bucket-test-1";
        conn.createBucket(bucketName);
        conn.createBucket(bucketName);
        testS3BucketList(conn);
        conn.deleteBucket(bucketName);

    }
    public static void testS3bucketMax(AmazonS3 conn){
        int n =11;
        for (n=1;n<11;n++){
            String bucketName = "tdk-bucket-"+n;
            conn.createBucket(bucketName);
        }
        testS3BucketList(conn);

        int i;
        for (i=1 ;i <11; i++){
            String bucketName = "tdk-bucket-"+i;
            testS3BucketDelete(conn,bucketName);
        }

    }



    public static void testS3ObjectDelete(AmazonS3 conn,String bucketName,String objName){
        conn.deleteObject(bucketName,objName);
    }

    public static  void testS3BucketList(AmazonS3 conn){
        //300
        int i ;
        List<Bucket> bucketList = conn.listBuckets();
        for (i = 0 ;i<bucketList.size();i++)
        {
            System.out.print(bucketList.get(i).getName() + "\n");
            System.out.println(bucketList.get(i).getCreationDate());
        }
    }

    public static void testS3BucketCreate(AmazonS3 conn,String bucketName){
        //301
        Bucket bucket = conn.createBucket(bucketName);
    }

    public static void testS3BucketDelete(AmazonS3 conn,String bucketName){
        //302
        conn.deleteBucket(bucketName);
    }




    }



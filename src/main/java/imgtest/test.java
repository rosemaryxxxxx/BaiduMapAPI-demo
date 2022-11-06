package imgtest;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.drew.imaging.ImageMetadataReader;
import com.drew.imaging.ImageProcessingException;
import com.drew.metadata.Directory;
import com.drew.metadata.Metadata;
import com.drew.metadata.Tag;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;




public class test {
    public static String apiKey = "98BTkroQl0ttEO1hgkrP3HZj4DM46gil";

//    public static class PhotoLocation
//    {
//        public final GeoLocation location;
//        public final File file;
//
//        public PhotoLocation(final GeoLocation location, final File file)
//        {
//            this.location = location;
//            this.file = file;
//        }
//    }

    public static void main(String[] args) throws ImageProcessingException, IOException {
        //自己的图片路径
        File file = new File("D:\\study\\研一\\Web\\imgset\\nonelocinfo.jpg");
        String filePath = file.getCanonicalPath();
        System.out.println("filePath: " + filePath);
//        try {
//            Metadata metadata = ImageMetadataReader.readMetadata(file);
//            for (Directory directory: metadata.getDirectories()){
//                for (Tag tag : directory.getTags()){
//                    System.out.format("[%s] - %s = %s",
//                            directory.getName(), tag.getTagName(), tag.getDescription());
//                    System.out.println(tag);
//                }
//                if (directory.hasErrors()) {
//                    for (String error : directory.getErrors()) {
//                        System.err.format("ERROR: %s", error);
//                    }
//                }
//            }
//        } catch (Exception e) {
//            System.out.println(e.toString());
//        }


//        Collection<GpsDirectory> gpsDirectories = metadata.getDirectoriesOfType(GpsDirectory.class);
//        Collection<PhotoLocation> photoLocations = new ArrayList<PhotoLocation>();
//        for (GpsDirectory gpsDirectory : gpsDirectories) {
//            // Try to read out the location, making sure it's non-zero
//            GeoLocation geoLocation = gpsDirectory.getGeoLocation();
//            if (geoLocation != null && !geoLocation.isZero()) {
//                // Add to our collection for use below
//                photoLocations.add(new PhotoLocation(geoLocation, file));
//                break;
//            }
//        }




        Metadata metadata = ImageMetadataReader.readMetadata(file);
        Double lat = null;
        Double lng = null;
        for (Directory directory : metadata.getDirectories()) {
            for (Tag tag : directory.getTags()) {
                String tagName = tag.getTagName();  //标签名
//                log.error("标签名： " + tagName);
                System.out.println("标签名： " + tagName);
                String desc = tag.getDescription(); //标签信息
                if (tagName.equals("Image Height")) {
                    System.out.println("图片高度: " + desc);
                } else if (tagName.equals("Image Width")) {
                    System.out.println("图片宽度: " + desc);
                } else if (tagName.equals("Date/Time")) {
                    System.out.println("拍摄时间: " + desc);
                }else if (tagName.equals("Date/Time Original")) {
                    System.out.println("拍摄时间: " + desc);
                } else if (tagName.equals("GPS Latitude")) {
                    System.err.println("纬度 : " + desc);
                    System.err.println("纬度(度分秒格式) : "+pointToLatlong(desc));
                    lat = latLng2Decimal(desc);
                } else if (tagName.equals("GPS Longitude")) {
                    System.err.println("经度: " + desc);
                    System.err.println("经度(度分秒格式): "+pointToLatlong(desc));
                    lng = latLng2Decimal(desc);
                } else if (tagName.equals("File Name")){
                    System.out.println("文件名：" + desc);
                } else if (tagName.equals("GPS Altitude")){
                    System.out.println("经度：" + desc);
                }
            }
        }

        convertGpsToLoaction(lat,lng);



    }
    /**
     * 经纬度格式  转换为  度分秒格式 ,如果需要的话可以调用该方法进行转换
     *
     * @param point 坐标点
     * @return
     */
    public static String pointToLatlong(String point) {
        Double du = Double.parseDouble(point.substring(0, point.indexOf("°")).trim());
        Double fen = Double.parseDouble(point.substring(point.indexOf("°") + 1, point.indexOf("'")).trim());
        Double miao = Double.parseDouble(point.substring(point.indexOf("'") + 1, point.indexOf("\"")).trim());
        Double duStr = du + fen / 60 + miao / 60 / 60;
        return duStr.toString();
    }

    /***
     * 经纬度坐标格式转换（* °转十进制格式）
     * @param gps
     */
    public static double latLng2Decimal(String gps) {
        String a = gps.split("°")[0].replace(" ", "");
        String b = gps.split("°")[1].split("'")[0].replace(" ", "");
        String c = gps.split("°")[1].split("'")[1].replace(" ", "").replace("\"", "");
        double gps_dou = Double.parseDouble(a) + Double.parseDouble(b) / 60 + Double.parseDouble(c) / 60 / 60;
        return gps_dou;
    }

    /**
     * api_key：注册的百度api的key
     * coords：经纬度坐标
     * http://api.map.baidu.com/reverse_geocoding/v3/?ak="+api_key+"&output=json&coordtype=wgs84ll&location="+coords
     * <p>
     * 经纬度转地址信息
     *
     * @param gps_latitude  维度
     * @param gps_longitude 精度
     */
    private static void convertGpsToLoaction(double gps_latitude, double gps_longitude) throws IOException {

        String res = "";
        String url = "https://api.map.baidu.com/reverse_geocoding/v3/?ak=" + apiKey + "&output=json&coordtype=wgs84ll&location=" + gps_latitude + "," + gps_longitude;
        System.err.println("【url】" + url);

        res = getURLContent(url);
        JSONObject object = JSONObject.parseObject(String.valueOf(res));
        if (object.containsKey("result")) {
            JSONObject result = object.getJSONObject("result");
            if (result.containsKey("addressComponent")) {
                JSONObject address = object.getJSONObject("result").getJSONObject("addressComponent");
                System.err.println("拍摄地点：" + address.get("country") + " " + address.get("province") + " " + address.get("city") + " " + address.get("district") + " "
                        + address.get("street") + " " + result.get("formatted_address") + " " + result.get("business"));
            }
        }
    }

    /**
     * 获取url中的json内容
     * @param url
     * @return
     */
    public static String getURLContent(String url) {
        StringBuilder json = new StringBuilder();
        try {
            URL urlObject = new URL(url);
            URLConnection uc = urlObject.openConnection();
            // 设置为utf-8的编码 解决中文乱码
            BufferedReader in = new BufferedReader(new InputStreamReader(uc.getInputStream(), "utf-8"));
            String inputLine = null;
            while ((inputLine = in.readLine()) != null) {
                json.append(inputLine);
            }
            in.close();
        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        return json.toString();
    }


}

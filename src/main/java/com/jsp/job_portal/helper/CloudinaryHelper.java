package com.jsp.job_portal.helper;

import java.util.Map;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import com.cloudinary.Cloudinary;
import com.cloudinary.utils.ObjectUtils;

@Service
@SuppressWarnings("rawtypes")
public class CloudinaryHelper {

    @Value("${CLOUDINARY_URL}")
    String url;

    public String saveImage(MultipartFile profilePic) {
        Cloudinary cloudinary = new Cloudinary(url);
        Map map = ObjectUtils.asMap("folder", "profile_pictures");
        try {
            Map x = cloudinary.uploader().upload(profilePic.getBytes(), map);
            return (String) x.get("url");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }

    public String savePdf(MultipartFile resume) {
        Cloudinary cloudinary = new Cloudinary(url);
        Map map = ObjectUtils.asMap("folder", "resumes");
        try {
            Map x = cloudinary.uploader().upload(resume.getBytes(), map);
            return (String) x.get("url");
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}

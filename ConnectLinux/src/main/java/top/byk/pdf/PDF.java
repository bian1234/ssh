//package top.byk.pdf;
//
//
//import java.io.File;
//import java.io.FileInputStream;
//import java.io.IOException;
//import java.net.URLDecoder;
//import java.net.URLEncoder;
//
//
///**
//   * @TODO:  测试pdf导出
//   * @author ykbian@qq.com
//   * @date:2019/3/19 19:45
//   * @param
//   * @return
//   */
//
//public class PDF {
//
//
//   public void exportPDF(HttpServletResponse response,HttpServletRequest request){
//
//       String basePath = pathFile;  //读取配置文件附件存储路径           //如路径 d:/data/file/oadoc/
//       String fileName="test.pdf";
//       String fileNameWithPath =basePath+fileName;
//       try {
//           // 转码（UTF-8-->GB2312），现在环境下的编码是UTF-8，但服务器操作系统的编码是GB2312
//           if(fileName!=null&&fileName.trim().length()>0){
//               fileName = URLEncoder.encode(fileName, "GB2312");
//               fileName = URLDecoder.decode(fileName, "UTF-8");
//           }else{
//               // fileName = "a."+FileUploadUtils.getExtension(location).toLowerCase();
//           }
//           File file = new File(fileNameWithPath);
//           FileInputStream fileinputstream = new FileInputStream(file);
//           long l = file.length();
//           int k = 0;
//           byte abyte0[] = new byte[65000];
//           response.setContentType("application/pdf");
//           response.setContentLength((int) l);
//           response.setHeader("Content-Disposition", "inline; filename="+ fileName);
//           while ((long) k < l) {
//               int j;
//               j = fileinputstream.read(abyte0, 0, 65000);
//               k += j;
//               response.getOutputStream().write(abyte0, 0, j);
//           }
//           fileinputstream.close();
//       } catch (IOException e) {
//           e.printStackTrace();
//       }
//		/*PageData pd=new PageData();
//		try {
//			logBefore(logger, Jurisdiction.getUsername() + "预览");
//			pd=testManageService.csbg(obj_id);
//			model.put("pd", pd);
//		} catch (Exception e) {
//			e.printStackTrace();
//		}*/
//    }
//    public static void main(String[] args) {
//
//    }
//}

# 使用文档

建议打包为AAR使用而不是直接导入源码

## 功能介绍

ImageNativeUtils中有的能力

- getHasPixelRectSize 获得图片中有像素的rect,遍历实现
- getConnectedArea,获得图片的所有连通域,深度优先实现,洪水填充算法
- getEdgePathByCv,获得图片边缘路径,opencv实现
- getEdgeBitmapByCv,获得原图和mask的边缘的叠加区域的图片,open CV实现
- getRgba2GrayByCv A8888转alpha格式,opencv实现

NCNNFaceDetectUtils中有的能力

- loadModel 初始化模型,因为考虑到体积大小问题,assets路径下只放了500m的推理文件,如果想尝试不同的推理文件,请看model_list文件夹

  第二个参数是模型index,第三个参数是使用cpu or gpu推理,在大部分机器上,CPU推理性能是要好于GPU的

  ```kotlin
  loadModel(assets, 0, 0)
  ```

  

- detailFaceByBitmap 获得图片中的人脸
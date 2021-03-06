显示一个图片列表的时候通常我们会在列表页显示较小的图片，点击进入图片详情页后再显示原始更清晰的图片

在进入图片详情页后通常的做法是先显示一个转转转的进度条，然后下载原始图片并显示，如果这时候网络较慢的话，用户会长时间在看一个空白的页面，体验不太好

要优化这个问题，最容易想到的办法就是先显示在列表页已经显示的较小的图片，等原始大图加载完后再显示原始大图

现在通过MemoryCacheStateImage就可以轻松实现这样的效果，首先MemoryCacheStateImage需要一个内存缓存key，就能从内存缓存中取出图片，然后用这张图片作为loading占位图显示，所以这里的关键就是怎么才能知道上一个页面中那些图片的内存缓存key呢？

首先我们需要知道内存缓存key的构成，其实内存缓存key就是请求ID
```java
public class DisplayInfo extends LoadInfo {

    public DisplayInfo() {
    }

    public DisplayInfo(DisplayInfo info) {
        super(info);
    }

    /**
     * 获取内存缓存key
     */
    public String getMemoryCacheKey() {
        return getId();
    }
}
```

请求ID是由uri和显示选项构成的
```
public class DisplayHelper {
    protected void preProcess() {
        ....

        // 根据URI和显示选项生成请求ID
        if (displayInfo.getId() == null) {
            displayInfo.setId(SketchUtils.makeRequestId(displayInfo.getUri(), displayOptions));
        }
    }
}
```
可以看到最终是由SketchUtils的makeRequestId()方法生成的请求ID
```java
public static String makeRequestId(String imageUri, DownloadOptions options) {
    StringBuilder builder = new StringBuilder();
    builder.append(imageUri);
    if (options != null) {
        options.makeId(builder);
    }
    return builder.toString();
}
```

从上面我们可以看到内存缓存key就是由uri和显示选项构成，因此我们可以拿到上一个页面ImageView的Options Id，然后在图片详情页将uri和Options Id拼接一下就能得到内存缓存key了

第一步，在图片列表页点击图片跳转的时候取出Options Id并传到图片详情页
```java
public void onClick(View v) {
    SketchImageView sketchImageView = (SketchImageView) v;
    String optionsId = sketchImageView.getOptionsId();

    Intent intent = new Intent(context, ImageDetailActivity.class);
    intent.put("optionsId", optionsId);
    intent.put("imageUrls", ....);
    startActivity(intent);
}
```
`一定要通过SketchImageView的getOptionsId()方法获取Options ID，因为在commit方法里会对Options进行补充处理，所以通过getOptionsId()得到的才是才是最终有效的Options Id`

第二步，假如ImageDetailActivity里面是一个ViewPager，然后又把Options Id和图片uri，传到了最终的ImageFragment中，那么就在ImageFragment中拼装内存缓存key
```java
public class ImageFragment extends Fragment {
    String optionsId;
    String imageUri;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);  

        optionsId = getArguments().getString("optionsId");
        imageUri = getArguments().getString("imageUri");
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        SketchImageView sketchImageView = view.findViewById(R.id.iamge);
        DisplayOptions options = sketchImageView.getOptions();

        String loadingImageMemoryCacheKey = SketchUtils.makeRequestId(imageUri, optionsId);       
        options.setLoadingImage(new MemoryCacheStateImage(loadingImageMemoryCacheKey));

        options.set....;
        sketchImageView.displayImage(imageUri);
    }
}
```
`SketchUtils还有一个重载的makeRequestId(String, String)方法可用来生成请求ID`

更详细的实现细节请参考示例app

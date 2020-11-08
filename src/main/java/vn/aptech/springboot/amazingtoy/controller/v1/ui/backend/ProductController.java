package vn.aptech.springboot.amazingtoy.controller.v1.ui.backend;

import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StringUtils;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import vn.aptech.springboot.amazingtoy.controller.v1.command.ProductStoreFormCommand;
import vn.aptech.springboot.amazingtoy.dto.mapper.UserMapper;
import vn.aptech.springboot.amazingtoy.dto.model.user.UserDto;
import vn.aptech.springboot.amazingtoy.model.category.Category;
import vn.aptech.springboot.amazingtoy.model.images.Image;
import vn.aptech.springboot.amazingtoy.model.products.BidDetail;
import vn.aptech.springboot.amazingtoy.model.products.Product;
import vn.aptech.springboot.amazingtoy.model.products.ProductType;
import vn.aptech.springboot.amazingtoy.model.subcategory.Subcategory;
import vn.aptech.springboot.amazingtoy.model.user.User;
import vn.aptech.springboot.amazingtoy.service.*;
import vn.aptech.springboot.amazingtoy.util.FileUtil;

import javax.validation.Valid;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping(value = "admin/product")
public class ProductController {

    private final String PRODUCTS_IMAGE_PATH = "backend/dist/img/products";

    @Autowired
    private ProductService productService;

    @Autowired
    private ImageService imageService;

    @Autowired
    private UserService userService;

    @Autowired
    private SubcategoryService subcategoryService;

    @Autowired
    private BidDetailService bidDetailService;

    @Autowired
    private CategoryService categoryService;

    // SHOW ALL
    @RequestMapping(value = {"", "/", "index"}, method =RequestMethod.GET)
    public String product(Model model) {
        List<Product> products = productService.findAllPro();
        model.addAttribute("products", products);
        return "backend/layout/pages/product/index";
    }

    //Show image list of product
    @RequestMapping(value = "/imageList/{productId}")
    public String displayImageList(Model model, @PathVariable("productId") Long id){
        Product product = productService.findPk(id);
        model.addAttribute("product", product);
        return "backend/layout/pages/image/index";
    }
    @RequestMapping(value = "/doDefaultImage/{idImage}", method = RequestMethod.GET)
    public String doDefaultImage(@PathVariable("idImage") String id){
        Optional<Image> image = imageService.findPk(Integer.parseInt(id));
        Product product = productService.findPk(image.get().getProduct().getId());
        for (Image image1 : product.getImagesCollection()) {
            if (image1.getImageId() == Integer.parseInt(id)) {
                image1.setMainImage(true);
            } else {
                image1.setMainImage(false);
            }
        }

        productService.create(product);
        return "redirect:/admin/products/imageList/" + product.getId();
    }

    //create image of product

    @RequestMapping(value = "/createImages/{productId}", method = RequestMethod.GET)
    public String createImages(Model model, @PathVariable("productId") Long id){
        model.addAttribute("productId", id);
        model.addAttribute("image", new Image());
        return "backend/layout/pages/image/create";
    }

    @RequestMapping(value = "/doCreateImage", method = RequestMethod.POST)
    public String doCreateImage(@ModelAttribute("image") Image image, RedirectAttributes ra, @RequestParam("files") MultipartFile[] files, @RequestParam("productId") Long id) throws IOException{
        Product product = productService.findPk(id);
        UserDto user = userService.findById(Long.parseLong("1"));

        for (MultipartFile file : files) {
            String imageName = FileUtil.UploadedFile(file, PRODUCTS_IMAGE_PATH);
            Image storeImage = new Image();
            storeImage.setName(imageName);
            storeImage.setDescription(image.getDescription());
            storeImage.setAboutId(1);
            storeImage.setBlogId(1);
            storeImage.setUserId(new ModelMapper().map(user, User.class));
            storeImage.setProduct(product);
            imageService.saveImage(storeImage);
        }
        return "redirect:/admin/product/imageList/" + product.getId();
    }



    //CREATE - GET

    @RequestMapping(value= "/create" , method = RequestMethod.GET)
    public String create(Model model) {
        ProductStoreFormCommand productStoreFormCommand = new ProductStoreFormCommand();
        List<Subcategory> subcategories = subcategoryService.findAllSubcat();
        productStoreFormCommand.setSubcategories(subcategories);

        model.addAttribute("productStoreFormCommand", productStoreFormCommand);
        return "backend/layout/pages/product/create";
    }


    //CREATE - POST

    @RequestMapping(value= "/create", method = RequestMethod.POST)
    public String create(@Valid ProductStoreFormCommand productStoreFormCommand, BindingResult bindingResult) {

        if (bindingResult.hasErrors()) {
            return "backend/layout/pages/product/create";
        } else {
            Product product = storedProduct(productStoreFormCommand);
            return "redirect:/admin/product";
        }
    }

    private Product storedProduct(ProductStoreFormCommand productStoreFormCommand) {
        Product product = new Product();
        Subcategory subcategory = subcategoryService.findPk(Integer.parseInt(productStoreFormCommand.getCategory()));

        if (productStoreFormCommand.getProductType() == ProductType.Auction) {

            product.setSlug(productStoreFormCommand.getSlug());
            product.setSku(productStoreFormCommand.getSku());
            product.setProductName(productStoreFormCommand.getProductName());
            product.setProductDescription(productStoreFormCommand.getProductDescription());
            product.setProductContent(productStoreFormCommand.getProductContent());
            product.setUnitPrice(productStoreFormCommand.getUnitPrice());
            product.setSavePrice(productStoreFormCommand.getSavePrice());
            product.setUnitWeight(productStoreFormCommand.getUnitWeight());
            product.setStock(productStoreFormCommand.getStock());
            product.setProductType(productStoreFormCommand.getProductType());
            BidDetail bidDetail = bidDetailService.stored(new BidDetail()
                    .setBidIncrement(productStoreFormCommand.getBidIncrement())
                    .setAuctionStart(productStoreFormCommand.getAuctionStart())
                    .setAuctionEnd(productStoreFormCommand.getAuctionEnd()));
            product.setBidDetail(bidDetail);
            product.setSubcategory(subcategory);
        } else {
            product.setSlug(productStoreFormCommand.getSlug());
            product.setSku(productStoreFormCommand.getSku());
            product.setProductName(productStoreFormCommand.getProductName());
            product.setProductDescription(productStoreFormCommand.getProductDescription());
            product.setProductContent(productStoreFormCommand.getProductContent());
            product.setUnitPrice(productStoreFormCommand.getUnitPrice());
            product.setSavePrice(productStoreFormCommand.getSavePrice());
            product.setUnitWeight(productStoreFormCommand.getUnitWeight());
            product.setStock(productStoreFormCommand.getStock());
            product.setProductType(productStoreFormCommand.getProductType());
            product.setSubcategory(subcategory);
        }

        if (productStoreFormCommand.getProductType() == ProductType.Auction) {
            product.setProductType(ProductType.Auction);
        } else {
            product.setProductType(ProductType.Sell);
        }

        return productService.create(product);
    }


    //UPDATE - GET
    @RequestMapping(value = "/displayUpdateProduct/{idUpdate}")
    public String displayUpdateProduct(Model model, @PathVariable("idUpdate") String id) {
        Product product = productService.findPk(Long.parseLong(id));
        if(product != null) {
            List<Subcategory> subcategoryList = subcategoryService.findAllSubcat();
            model.addAttribute("subcategory",subcategoryList);
            model.addAttribute("product", product);
        }
        return "backend/layout/pages/product/update";
    }

    //UPDATE - POST

    @RequestMapping(value = "/doUpdateProduct", method = RequestMethod.POST)
    public String doUpdate(Model model,
                           @ModelAttribute("product") Product product) {
        Subcategory subcategory = subcategoryService.findPk(product.getSubcategory().getSubcatId());
        product.setSubcategory(subcategory);
        productService.update(product);
        return "redirect:/admin/product";

    }

    //DELETE
    @RequestMapping(value = "/delete/{idDelete}", method = RequestMethod.GET)
    public String deleteProduct(RedirectAttributes redirectAttributes, @PathVariable("idDelete") String id) {
        Product product = productService.findPk(Long.parseLong(id));

        productService.delete(Long.parseLong(id));
        redirectAttributes.addFlashAttribute("success", "Deleted product " + product.getProductName() + " successfully");

        return "redirect:/admin/product";
    }

}


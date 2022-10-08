package com.naukma.aic.controller;

import com.naukma.aic.entity.*;
import com.naukma.aic.entity.order.Receipt;
import com.naukma.aic.entity.order.ReceiptProduct;
import com.naukma.aic.exceptionHandlers.exceptions.ReceiptNotFoundException;
import com.naukma.aic.exceptionHandlers.exceptions.ReceiptProductNotFoundException;
import com.naukma.aic.exceptionHandlers.exceptions.UserNotFoundException;
import com.naukma.aic.repository.*;
import com.naukma.aic.service.CategoryService;
import com.naukma.aic.service.ReceiptService;
import com.naukma.aic.service.UserService;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.repository.query.Param;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.view.RedirectView;

import javax.validation.Valid;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Controller
@Log4j2
public class ReceiptController {

    @Autowired
    private CategoryRepository categoryRepository;

    @Autowired
    private CategoryService categoryService;

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private ReceiptService receiptService;
    @Autowired
    private ReceiptRepository receiptRepository;
    @Autowired
    private RecProdRepository recProdRepository;
    @Autowired
    private ProductRepository productRepository;

    @ModelAttribute("userIpn")
    public String getCurrentUserIpn(Authentication authentication) {
        UserDetails det =(authentication == null) ? null : (UserDetails) authentication.getPrincipal();
        if(det == null){
            return "";
        }else{
            String email = det.getUsername();
            if(!userRepository.findByEmail(email).isPresent()){
                throw new UserNotFoundException(email);
            }
            else
                return userRepository.findByEmail(email).get().getIpn();
        }
    }

    @ModelAttribute("currentUser")
    public UserDetails getCurrentUser(Authentication authentication) {
        return (authentication == null) ? null : (UserDetails) authentication.getPrincipal();
    }

    @GetMapping("/receipt/all")
    public ModelAndView getAllReceipt(@RequestParam(value = "sortParam", required = false) String sortParam,
                                      @RequestParam(value = "sortType", required = false) String sortType) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("receipt_all");
        mav.addObject("sortParam", sortParam);
        mav.addObject("sortType", sortType);
        Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        String username = null;
        if (principal instanceof UserDetails) {
            username = ((UserDetails) principal).getUsername();
        }
        User user = userService.findUserByEmail(username);
        if (user.getStatus().equals("USER"))
            mav.addObject("receipts", receiptService.getAllReceipts(sortParam, sortType, user.getIpn()));
        else
            mav.addObject("receipts", receiptService.getAllReceipts(sortParam, sortType, null));
        return mav;
    }

    @GetMapping("/receipt/{id}/products")
    public ModelAndView getReceiptProducts(@PathVariable Long id) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("receipt_products");

        if (!receiptRepository.findById(id).isPresent()) {
            throw new ReceiptNotFoundException(id);
        }

        ReceiptDTO receipt = receiptRepository.getReceiptInfo(id);
        mav.addObject("receipt", receipt);
        List<ReceiptProductDTO> lines = new ArrayList<>();
        if (receipt.getProductAmount() != 0)
            lines = receiptRepository.getProductsInReceipt(id);
        mav.addObject("lines", lines);
        return mav;
    }

    @PutMapping("/receipt/notes/{id}")
    public RedirectView updateReceiptNotes(@PathVariable Long id, ReceiptInfoDTO dto) {
        log.info(dto);
        receiptService.editReceipt(id, dto);
        return new RedirectView("/receipt/" + id + "/products");
    }

    @GetMapping("/receipt/add")
    public ModelAndView getCreateReceiptPageAdmin() {
        ModelAndView mav = new ModelAndView();
        ReceiptInfoDTO receipt = new ReceiptInfoDTO();
        mav.setViewName("receipt_create");
        mav.addObject("receipt", receipt);
        mav.addObject("users", userRepository.findUsersByStatus("USER"));
        return mav;
    }

    @PostMapping("/receipt/add")
    public RedirectView createReceipt(@ModelAttribute("receipt") ReceiptInfoDTO receipt) {
        receiptService.createReceipt(receipt);
        return new RedirectView("/receipt/all");
    }

    @PostMapping("/receipt/{id}")
    public RedirectView updateReceiptProducts(@PathVariable Long id, @ModelAttribute("properties_wrapper")
            ReceiptLineWrapper propertiesWrapper) {
        //Just print the values
        receiptService.updateProductsReceipt(id, propertiesWrapper.getProperties());
        return new RedirectView("/receipt/" + id);
    }

    @DeleteMapping("/receipt/products")
    public RedirectView deleteReceiptProduct(@RequestParam(value = "id") String Sid, @RequestParam(value = "productId") String productId) {
        Long id = Long.valueOf(Sid);
        if (!receiptRepository.findById(id).isPresent()) {
            throw new ReceiptNotFoundException(id);
        }
        if (!recProdRepository.findReceiptProductsById_ReceiptIdAndId_ProductId(id, productId).isPresent()) {
            throw new ReceiptProductNotFoundException(id, productId);
        }
        ReceiptProduct pr = recProdRepository.findReceiptProductsById_ReceiptIdAndId_ProductId(id, productId).get();
        recProdRepository.delete(pr);

        Receipt receipt = receiptRepository.findById(id).get();
        List<ReceiptProduct> allProd = recProdRepository.findReceiptProductsById_ReceiptId(id);
        double resPrice = allProd.stream().mapToDouble(ReceiptProduct::getPriceForLine).sum();
        receipt.setTotalPrice(resPrice);
        receiptRepository.save(receipt);

        return new RedirectView("/receipt/" + id + "/products");
    }

    @DeleteMapping("/receipt/{id}")
    public RedirectView deleteReceipt(@PathVariable Long id) {
        receiptService.deleteReceipt(id);
        return new RedirectView("/receipt/all");
    }

    @GetMapping("/receipt/{id}")
    public ModelAndView goToEditPageOfReceipt(@PathVariable Long id, Model model) {
        ModelAndView mav = new ModelAndView();
        mav.setViewName("receipt_edit");

        if (!receiptRepository.findById(id).isPresent()) {
            throw new ReceiptNotFoundException(id);
        }

        ReceiptDTO receipt = receiptRepository.getReceiptInfo(id);
        mav.addObject("receipt", receipt);

        ArrayList<ReceiptLineDTO> lines = new ArrayList<>();
        if (receipt.getProductAmount() != 0) {
            List<ReceiptProductDTO> res = receiptRepository.getProductsInReceipt(id);
            for (ReceiptProductDTO line : res) {
                ReceiptLineDTO dto = new ReceiptLineDTO(line.getArticle(), line.getName(), line.getProducer(), line.getAmount(), line.getPriceForOne());
                lines.add(dto);
            }
        }
        ReceiptLineWrapper prwrapper = createWrapper(lines);

        ReceiptLineDTO newProduct = new ReceiptLineDTO();

        List<ProductDTO> products = productRepository.getAllProducts();
        List<String> productsInOrder = productRepository.getProductsInOrder(id).stream()
                .map(ProductDTO::getArticle).collect(Collectors.toList());
        products.removeIf(x -> productsInOrder.contains(x.getArticle()));

        model.addAttribute("products", products);
        model.addAttribute("addProduct", newProduct);
        model.addAttribute("properties_wrapper", prwrapper);
        return mav;
    }

    @PostMapping("/receipt/{id}/add/product")
    public RedirectView createReceipt(@PathVariable Long id, @ModelAttribute("addProduct") ReceiptLineDTO product) {
        receiptService.addProduct(id,product);
        return new RedirectView("/receipt/" + id + "/products");
    }

    private ReceiptLineWrapper createWrapper(ArrayList<ReceiptLineDTO> lines) {
        ReceiptLineWrapper res = new ReceiptLineWrapper();
        res.setProperties(lines);
        return res;
    }

}
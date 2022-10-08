package com.naukma.aic.service;

import com.naukma.aic.entity.*;
import com.naukma.aic.entity.order.Receipt;
import com.naukma.aic.entity.order.ReceiptProduct;
import com.naukma.aic.entity.order.ReceiptProductId;
import com.naukma.aic.exceptionHandlers.exceptions.*;
import com.naukma.aic.repository.CategoryRepository;
import com.naukma.aic.repository.ProductRepository;
import com.naukma.aic.repository.RecProdRepository;
import com.naukma.aic.repository.ReceiptRepository;
import lombok.extern.log4j.Log4j2;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Sort;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.view.RedirectView;

import javax.transaction.Transactional;
import java.time.LocalDate;
import java.util.*;
import java.util.stream.Collectors;

@Service
@Log4j2
public class ReceiptService {

    @Autowired
    private ProductRepository productRepository;

    @Autowired
    private RecProdRepository recProdRepository;

    @Autowired
    private UserService userService;

    @Autowired
    private CategoryRepository categoryRepository;

    private static List<String> sortParams = Arrays.asList("id", "user", "date", "total_price");

    @Autowired
    private ReceiptRepository receiptRepository;

    @Transactional
    public void deleteReceipt(Long id) {
        Optional<Receipt> receipt = receiptRepository.findById(id);
        if (!receipt.isPresent()) {
            throw new ReceiptNotFoundException(id);
        } else {
            receiptRepository.deleteById(id);
        }
    }

    public List<Receipt> findAll() {
        return (List<Receipt>) receiptRepository.findAll();
    }

    public void createReceipt(ReceiptInfoDTO dto) {
        if (dto.getUserIpn() == null) {
            Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
            String username = null;
            if (principal instanceof UserDetails) {
                username = ((UserDetails) principal).getUsername();
            }
            User user = userService.findUserByEmail(username);
            if (user == null) {
                return;
            } else {
                dto.setUserIpn(user.getIpn());
            }
        }
        Receipt receipt = new Receipt();
        receipt.setUser(userService.findUserByIpn(dto.getUserIpn()));
        receipt.setNotes(dto.getNotes());
        receipt.setDate(LocalDate.now());
        receiptRepository.save(receipt);
    }

    public void updateProductsReceipt(Long id, ArrayList<ReceiptLineDTO> dtos) {
        Map<String, Integer> newVal = dtos.stream().collect(Collectors.toMap(ReceiptLineDTO::getArticle, ReceiptLineDTO::getAmount));

        if (!receiptRepository.findById(id).isPresent()) {
            throw new ReceiptNotFoundException(id);
        }
        List<ReceiptProduct> pr = recProdRepository.findAllById_ReceiptIdAndId_ProductIdIn(id, newVal.keySet());
        for (ReceiptProduct prod : pr) {
            prod.setAmount(newVal.get(prod.getId().getProductId()));
            prod.setPriceForLine(prod.getAmount() * prod.getPriceForOne());
        }
        recProdRepository.saveAll(pr);

        Receipt receipt = receiptRepository.findById(id).get();
        List<ReceiptProduct> allProd = recProdRepository.findReceiptProductsById_ReceiptId(id);
        double resPrice = allProd.stream().mapToDouble(ReceiptProduct::getPriceForLine).sum();
        receipt.setTotalPrice(resPrice);
        receiptRepository.save(receipt);
    }

    public boolean editReceipt(Long id, ReceiptInfoDTO newNotes) {
        Receipt receipt;
        if (receiptRepository.findById(id).isPresent()) {
            receipt = receiptRepository.findById(id).get();
        } else {
            throw new ReceiptNotFoundException(id);
        }
        if (newNotes.getNotes().equals(receipt.getNotes()))
            return true;
        else if (newNotes.getNotes().isBlank())
            receipt.setNotes(null);
        else
            receipt.setNotes(newNotes.getNotes());
        receiptRepository.save(receipt);
        return true;
    }

    public void addProduct(Long id, ReceiptLineDTO dto) {


        if (!receiptRepository.findById(id).isPresent()) {
            throw new ReceiptNotFoundException(id);
        }
        if (!productRepository.findProductByArticle(dto.getArticle()).isPresent()) {
            throw new ProductNotFoundException(dto.getArticle());
        }
        if (dto.getAmount() <= 0) {
            throw new InvalidData(Collections.singletonMap("amount", dto.getAmount().toString()));
        }
        Product product = productRepository.findProductByArticle(dto.getArticle()).get();
        Receipt receipt = receiptRepository.findById(id).get();

        ReceiptProductId prId = new ReceiptProductId();
        prId.setReceiptId(id);
        prId.setProductId(product.getArticle());
        ReceiptProduct pr = new ReceiptProduct();
        pr.setId(prId);
        pr.setProduct(product);
        pr.setAmount(dto.getAmount());
        pr.setPriceForOne(product.getPrice());
        pr.setPriceForLine(pr.getPriceForOne()*pr.getAmount());
        recProdRepository.save(pr);
        List<ReceiptProduct> allProd = recProdRepository.findReceiptProductsById_ReceiptId(id);
        double resPrice = allProd.stream().mapToDouble(ReceiptProduct::getPriceForLine).sum();
        receipt.setTotalPrice(resPrice);
        receiptRepository.save(receipt);
    }


    public List<ReceiptDTO> getAllReceipts(String sortParam, String sortType, String userIpn) {
        if (sortParam == null && sortType == null) {
            if (userIpn == null)
                return receiptRepository.getAllReceiptsInfo();
            else
                return receiptRepository.getAllReceiptsInfo(userIpn);
        }
        if (sortParam != null && sortType == null)
            sortType = "ASC";
        if (!sortParams.contains(sortParam)) {
            throw new InvalidData(Collections.singletonMap("sortParam", sortParam));
        }
        if (sortType.equals("ASC") || sortType.equals("DESC")) {
            if (userIpn != null)
                return receiptRepository.getAllReceiptsInfoSorted(PageRequest.of(0, 10, Sort.Direction.valueOf(sortType), sortParam), userIpn);
            else
                return receiptRepository.getAllReceiptsInfoSorted(PageRequest.of(0, 10, Sort.Direction.valueOf(sortType), sortParam));
        } else
            throw new InvalidData(Collections.singletonMap("sortType", sortType));
    }
}

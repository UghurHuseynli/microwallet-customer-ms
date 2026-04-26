package az.abb.customer.service;

import az.abb.customer.dto.request.DocumentRequest;
import az.abb.customer.dto.response.DocumentResponse;
import az.abb.customer.feign.PaymentFeignClient;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.io.RandomAccessReadBuffer;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;

@Service
@RequiredArgsConstructor
@Slf4j
public class DocumentService {

    private final PaymentFeignClient paymentFeignClient;

    public DocumentResponse processDocument(MultipartFile file, Long userId, String description) {

        String extractedText = extractText(file);

        DocumentRequest dto = DocumentRequest.builder()
                .fileName(file.getOriginalFilename())
                .description(description)
                .fileSize(file.getSize())
                .contentType(file.getContentType())
                .extractedText(extractedText)
                .build();

        return paymentFeignClient.processDocument(userId, dto);
    }

    private String extractText(MultipartFile file) {
        String contentType = file.getContentType();
        try {
            if (contentType == null) return "";

            if (contentType.equals("application/pdf")) {
                InputStream is = file.getInputStream();
                PDDocument document = Loader.loadPDF(new RandomAccessReadBuffer(is));
                PDFTextStripper pdfStripper = new PDFTextStripper();
                String text = pdfStripper.getText(document);
                document.close();
                return text;
            }

            // Plain text / CSV / JSON
            if (contentType.startsWith("text/")) {
                return new String(file.getBytes(), StandardCharsets.UTF_8);
            }

            // Word documents (.docx)
            if (contentType.equals("application/vnd.openxmlformats-officedocument.wordprocessingml.document")) {
                XWPFDocument doc = new XWPFDocument(file.getInputStream());
                XWPFWordExtractor extractor = new XWPFWordExtractor(doc);
                return extractor.getText();
            }

            return "";
        } catch (IOException e) {
            throw new RuntimeException("Failed to extract text from file: " + file.getOriginalFilename(), e);
        }
    }
}

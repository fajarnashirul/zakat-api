package com.nashirul.zakat.service.implemetation;

import com.nashirul.zakat.dto.PaymentTransactionDto;
import com.nashirul.zakat.entity.PaymentTransaction;
import com.nashirul.zakat.service.EmailService;
import org.apache.commons.mail.DefaultAuthenticator;
import org.apache.commons.mail.EmailException;
import org.apache.commons.mail.HtmlEmail;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

@Service
public class EmailImpl implements EmailService {

    @Value("${email.username}")
    private String hostEmail;
    @Value("${email.password}")
    private String hostPassword;
    @Override
    public void sendEmailPaymentUrl(PaymentTransactionDto paymentTransactionDto, String paymentUrl) throws EmailException {
        HtmlEmail email = new HtmlEmail();
        email.setHostName("smtp.gmail.com"); // Your Host Email
        email.setSmtpPort(587); // SMTP port
        email.setAuthenticator(new DefaultAuthenticator(hostEmail, hostPassword)); // email credential
        email.setStartTLSEnabled(true);

        email.setFrom(hostEmail);
        email.addTo(paymentTransactionDto.getEmail());
        email.setSubject("Payment");

        String htmlMessage = "<p>Pembayaran telah dibuat mohon untuk segera diproses dengan mengakses link yang tersedia.</p>";
        htmlMessage += "<p>Detail Pembayaran:</p>";
        htmlMessage += "<ul>";
        htmlMessage += "<li>Nomor Transaksi: " + paymentTransactionDto.getId() + "</li>";
        htmlMessage += "<li>Jumlah Pembayaran: " + paymentTransactionDto.getAmount() + "</li>";
        htmlMessage += "<li>Link Pembayaran: " + paymentUrl + "</li>";

        htmlMessage += "</ul>";

        email.setHtmlMsg(htmlMessage);

        // Optional: Set text message
        email.setTextMsg("Untuk melakukan pembayaran silahkan mengkases link dibawah ini");
        email.setTextMsg("Link Pembayaran: " + paymentUrl);

        email.send();
    }

    @Override
    public void sendEmailPaymentSuccess(PaymentTransaction paymentTransaction) throws EmailException {
        HtmlEmail email = new HtmlEmail();
        email.setHostName("smtp.gmail.com"); // Your Host Email
        email.setSmtpPort(587); // SMTP port
        email.setAuthenticator(new DefaultAuthenticator(hostEmail, hostPassword)); // email credential
        email.setStartTLSEnabled(true);

        email.setFrom(hostEmail);
        email.addTo(paymentTransaction.getEmail());
        email.setSubject("Payment");

        String htmlMessage = "<p>Pembayaran telah berhasil, terimakasih telah mempercayai layanan kami</p>";
        htmlMessage += "<p>Detail Pembayaran:</p>";
        htmlMessage += "<ul>";
        htmlMessage += "<li>Nomor Transaksi: " + paymentTransaction.getId() + "</li>";
        htmlMessage += "<li>Jumlah Pembayaran: " + paymentTransaction.getAmount() + "</li>";

        htmlMessage += "</ul>";

        email.setHtmlMsg(htmlMessage);

        // Optional: Set text message
        email.setTextMsg("Pembayaran telah berhasil, terimakasih telah mempercayai layanan kami");

        email.send();
    }
}

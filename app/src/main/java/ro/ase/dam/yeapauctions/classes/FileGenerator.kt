package ro.ase.dam.yeapauctions.classes

import android.content.ActivityNotFoundException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.FileOutputStream
import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.drawable.BitmapDrawable
import android.icu.text.SimpleDateFormat
import android.util.Log
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.core.content.FileProvider
import com.itextpdf.io.image.ImageDataFactory
import com.itextpdf.io.source.ByteArrayOutputStream
import com.itextpdf.kernel.colors.ColorConstants
import com.itextpdf.kernel.colors.DeviceRgb
import com.itextpdf.kernel.pdf.PdfDocument
import com.itextpdf.kernel.pdf.PdfWriter
import com.itextpdf.layout.Document
import com.itextpdf.layout.borders.Border
import com.itextpdf.layout.element.Cell
import com.itextpdf.layout.element.Image
import com.itextpdf.layout.element.Paragraph
import com.itextpdf.layout.element.Table
import com.itextpdf.layout.property.HorizontalAlignment
import com.itextpdf.layout.property.TextAlignment
import ro.ase.dam.yeapauctions.R
import ro.ase.dam.yeapauctions.data.Datasource
import ro.ase.dam.yeapauctions.data.Datasource.addresses
import ro.ase.dam.yeapauctions.data.Datasource.auctions
import ro.ase.dam.yeapauctions.data.Datasource.links
import ro.ase.dam.yeapauctions.data.Datasource.lots
import ro.ase.dam.yeapauctions.data.Datasource.offers
import java.io.File

object FileGenerator {
    val dateFormat = SimpleDateFormat("dd/MM/yyyy")

    suspend fun writeXlsx(
        context: Context,
        payments: SnapshotStateList<Payment>
    ) {
        withContext(Dispatchers.IO) {
            val workbook = XSSFWorkbook()
            val sheet = workbook.createSheet("MyPayments")
            val row = sheet.createRow(0)
            //TODO hardcoded strings
            row.createCell(0).setCellValue("PO")
            row.createCell(1).setCellValue("AUCTION")
            row.createCell(2).setCellValue("VAT")
            row.createCell(3).setCellValue("MARKUP")
            row.createCell(4).setCellValue("MARKUP_VAT")
            row.createCell(5).setCellValue("TOTAL")
            row.createCell(6).setCellValue("PAID")
            row.createCell(7).setCellValue("CREATED")

            // Add more rows as needed
            var cnt = 1
            if(!payments.isNullOrEmpty()){
                payments?.forEach { payment ->
                    var auction: Auction? =
                        Datasource.auctions.firstOrNull { it.id == payment.auctionId }
                    val row = sheet.createRow(cnt++)
                    row.createCell(0).setCellValue(payment.number.toString())
                    if (auction != null) {
                        row.createCell(1).setCellValue(auction.number.toString())
                    }

                    row.createCell(2).setCellValue(payment.totalVAT)
                    row.createCell(3).setCellValue(payment.totalMarkup)
                    row.createCell(4).setCellValue(payment.totalMarkupVAT)
                    row.createCell(5).setCellValue(payment.grandTotal)
                    row.createCell(6).setCellValue(payment.paid)
                    row.createCell(7).setCellValue(dateFormat.format(payment.createdAt))
                }
            }

            val myFilesDir = File(context.getExternalFilesDir(null), "my_files")
            if (!myFilesDir.exists()) {
                myFilesDir.mkdir()
            }
            val file = File(myFilesDir, "Payments.xlsx")
            val fos = FileOutputStream(file)
            workbook.write(fos)
            fos.close()
            workbook.close()


            val fileUri = FileProvider.getUriForFile(
                context,
                context.applicationContext.packageName + ".provider",
                file
            )

            val openIntent = Intent(Intent.ACTION_VIEW).apply {
                setDataAndType(fileUri, "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet")
                flags = Intent.FLAG_GRANT_READ_URI_PERMISSION or Intent.FLAG_ACTIVITY_CLEAR_TOP
            }


            try {
                context.startActivity(openIntent)
            } catch (e: ActivityNotFoundException) {
                Log.d("XLSX-opening", "error" + e.message)
            }
        }
    }

    suspend fun writePdf(
        context: Context,
        payment: Payment,
        user: User
    ){
        val auction = auctions.firstOrNull { it.id == payment.auctionId }
        val address = addresses.firstOrNull{ it.id == user.addressId}
        val userLinks = links.filter { it.userId == user.id && it.auctionId == payment.auctionId && it.won}
        val lotIds = userLinks.map { it.lotId }
        val invoicePurple = DeviceRgb(103, 80, 164)
        val invoiceGray = DeviceRgb(220,220,220)


        withContext(Dispatchers.IO) {
            val myFilesDir = File(context.getExternalFilesDir(null), "my_files")
            if (!myFilesDir.exists()) {
                myFilesDir.mkdir()
            }
            val file = File(myFilesDir, "Invoice" + "#" + payment.number.toString() + ".pdf")
            val fos = FileOutputStream(file)

            val writer = PdfWriter(file)
            val pdfDocument = PdfDocument(writer)
            val document = Document(pdfDocument)

            val columnWidth = floatArrayOf(140f, 140f, 140f, 140f)
            val table1 = Table(columnWidth)

            //Table1-----01
            val d1 = context.getDrawable(R.drawable.logo_invoice)
            val bitmap1 = (d1 as BitmapDrawable).bitmap
            val stream1 = ByteArrayOutputStream()
            bitmap1.compress(Bitmap.CompressFormat.PNG, 100, stream1)
            val bitmapData1 = stream1.toByteArray()

            val imageData1 = ImageDataFactory.create(bitmapData1)
            val image1 = Image(imageData1)
            image1.setWidth(100f)

            table1.addCell(Cell(4,1).add(image1).setBorder(Border.NO_BORDER))
            table1.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table1.addCell(
                Cell(1,2)
                    .add(Paragraph("Invoice").setFontSize(26f).setFontColor(invoicePurple))
                    .setBorder(Border.NO_BORDER)
            )

            //Table1-----02
            table1.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table1.addCell(Cell().add(Paragraph("Invoice No:")).setBorder(Border.NO_BORDER))
            table1.addCell(Cell().add(Paragraph(payment.number.toString())).setBorder(Border.NO_BORDER))

            //Table1-----03
            table1.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table1.addCell(Cell().add(Paragraph("Invoice Date:")).setBorder(Border.NO_BORDER))
            table1.addCell(Cell().add(Paragraph(dateFormat.format(payment.createdAt))).setBorder(Border.NO_BORDER))

            //Table1-----04
            table1.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table1.addCell(Cell().add(Paragraph("Account No:")).setBorder(Border.NO_BORDER))
            table1.addCell(Cell().add(Paragraph(user.number.toString())).setBorder(Border.NO_BORDER))

            //Table1-----05
            table1.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table1.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table1.addCell(Cell().add(Paragraph("Auction No:")).setBorder(Border.NO_BORDER))
            if (auction != null) {
                table1.addCell(Cell().add(Paragraph(auction.number.toString())).setBorder(Border.NO_BORDER))
            }

            //Table1-----06
            table1.addCell(Cell().add(Paragraph("To")).setBorder(Border.NO_BORDER))
            table1.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table1.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table1.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))

            //Table1-----07
            table1.addCell(Cell().add(Paragraph(user.firstName + " " + user.lastName)).setBorder(Border.NO_BORDER))
            table1.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table1.addCell(
                Cell().add(Paragraph("Paid With:").setBold())
                    .setBorder(Border.NO_BORDER)
            )
            table1.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))


            //Table1-----08
            if (address != null) {
                table1.addCell(Cell().add(Paragraph(address.number + ", " + address.street)).setBorder(Border.NO_BORDER))
            }
            table1.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table1.addCell(Cell(1,2).add(Paragraph("Stripe")).setBorder(Border.NO_BORDER))

            //Table1-----09
            if (address != null) {
                table1.addCell(Cell().add(Paragraph(address.city + ", " + address.country)).setBorder(Border.NO_BORDER))
            }
            table1.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table1.addCell(Cell(1, 2).add(Paragraph("Card Payment: Visa")).setBorder(Border.NO_BORDER))

            val columnWidth2 = floatArrayOf(62f, 158f, 68f, 68f, 68f, 68f, 68f)
            val table2 = Table(columnWidth2)

            //Table 2 --------- 01
            table2.addCell(Cell().add(Paragraph("Lot No.").setFontColor(ColorConstants.WHITE)).setBackgroundColor(invoicePurple))
            table2.addCell(Cell().add(Paragraph("Name").setFontColor(ColorConstants.WHITE)).setBackgroundColor(invoicePurple))
            table2.addCell(Cell().add(Paragraph("Amount").setFontColor(ColorConstants.WHITE)).setBackgroundColor(invoicePurple))
            table2.addCell(Cell().add(Paragraph("VAT").setFontColor(ColorConstants.WHITE)).setBackgroundColor(invoicePurple))
            table2.addCell(Cell().add(Paragraph("Markup").setFontColor(ColorConstants.WHITE)).setBackgroundColor(invoicePurple))
            table2.addCell(Cell().add(Paragraph("Markup VAT").setFontColor(ColorConstants.WHITE)).setBackgroundColor(invoicePurple))
            table2.addCell(Cell().add(Paragraph("Total").setFontColor(ColorConstants.WHITE)).setBackgroundColor(invoicePurple))

            var lot : Lot?
            var offer: Offer?
            lotIds.forEach{ lotId ->
                lot = lots.firstOrNull{ it.id == lotId }
                if(lot != null){
                    table2.addCell(Cell().add(Paragraph(lot?.number.toString())).setBackgroundColor(invoiceGray))
                    table2.addCell(Cell().add(Paragraph(lot?.name)).setBackgroundColor(invoiceGray))
                }
                offer = offers.firstOrNull{it.lotId == lotId}
                table2.addCell(Cell().add(Paragraph(offer?.amount.toString())).setBackgroundColor(invoiceGray))
                table2.addCell(Cell().add(Paragraph(offer?.VAT.toString())).setBackgroundColor(invoiceGray))
                table2.addCell(Cell().add(Paragraph(offer?.markup.toString())).setBackgroundColor(invoiceGray))
                table2.addCell(Cell().add(Paragraph(offer?.markupVAT.toString())).setBackgroundColor(invoiceGray))
                table2.addCell(Cell().add(Paragraph(offer?.total.toString())).setBackgroundColor(invoiceGray))
            }

            table2.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table2.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table2.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table2.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table2.addCell(Cell(1,2).add(Paragraph(context.getString(R.string.auction_costs) + " (18.0%)").setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(invoicePurple))
            table2.addCell(Cell().add(Paragraph(payment.totalMarkup.toString() + " $").setFontColor(ColorConstants.WHITE)).setBackgroundColor(invoicePurple))

            table2.addCell(Cell(1,2).add(Paragraph(context.getString(R.string.terms))).setBorder(Border.NO_BORDER))
            table2.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table2.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table2.addCell(Cell(1,2).add(Paragraph(context.getString(R.string.vat_auction) + " (19.0%)").setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(invoicePurple))
            table2.addCell(Cell().add(Paragraph(payment.totalMarkupVAT.toString() + " $").setFontColor(ColorConstants.WHITE))
                .setBackgroundColor(invoicePurple))

            table2.addCell(Cell(1, 2).add(Paragraph(context.getString(R.string.terms_lots))).setBorder(Border.NO_BORDER))
            table2.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table2.addCell(Cell().add(Paragraph("")).setBorder(Border.NO_BORDER))
            table2.addCell(Cell(1,2).add(Paragraph(context.getString(R.string.grand_total)).setFontColor(ColorConstants.WHITE).setBold().setFontSize(16f))
                .setBackgroundColor(invoicePurple))
            table2.addCell(Cell().add(Paragraph(payment.grandTotal.toString() + " $")).setBackgroundColor(invoicePurple).setFontColor(ColorConstants.WHITE))

            val columnWidth3 = floatArrayOf(50f, 250f, 260f)
            val table3 = Table(columnWidth3)

            val d2 = context.getDrawable(R.drawable.contact)
            val bitmap2 = (d2 as BitmapDrawable).bitmap
            val stream2 = ByteArrayOutputStream()
            bitmap2.compress(Bitmap.CompressFormat.PNG, 100, stream2)
            val bitmapData2 = stream2.toByteArray()

            val imageData2 = ImageDataFactory.create(bitmapData2)
            val image2 = Image(imageData2)
            image2.setHeight(120f)

            val d3 = context.getDrawable(R.drawable.thanks)
            val bitmap3 = (d3 as BitmapDrawable).bitmap
            val stream3 = ByteArrayOutputStream()
            bitmap3.compress(Bitmap.CompressFormat.PNG, 100, stream3)
            val bitmapData3 = stream3.toByteArray()

            val imageData3 = ImageDataFactory.create(bitmapData3)
            val image3 = Image(imageData3)
            image3.setHeight(120f)
            image3.setHorizontalAlignment(HorizontalAlignment.RIGHT)

            table3.addCell(Cell(3,1).add(image2).setBorder(Border.NO_BORDER))
            table3.addCell(Cell().add(Paragraph("rbwauctions@gmail.com\nchris.neagu@outlook.com")).setBorder(Border.NO_BORDER))
            table3.addCell(Cell(3,1).add(image3).setBorder(Border.NO_BORDER))
            table3.addCell(Cell().add(Paragraph("+40757232710\n+40762205027")).setBorder(Border.NO_BORDER))
            table3.addCell(Cell().add(Paragraph("051765 Zidarului Street\nBucharest, Romania")).setBorder(Border.NO_BORDER))

            document.add(table1)
            document.add(Paragraph("\n"))
            document.add(table2)
            document.add(
                Paragraph(
                    "\n\n\n\n\n\n("+ context.getString(R.string.authorised) + ")\n\n\n"
                ).setTextAlignment(TextAlignment.RIGHT)
            )
            document.add(table3)
            document.close()
            fos.close()

        }
    }
}
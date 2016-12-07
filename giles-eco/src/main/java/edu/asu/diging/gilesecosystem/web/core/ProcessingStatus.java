package edu.asu.diging.gilesecosystem.web.core;

/**
 * Enum to represent the processing status of a file. The status should follow
 * this order:
 * <ul>
 *  <li>STORED: file has been stored in Nepomuk</li>
 *  <li>TEXT_EXTRACTION_COMPLETE: in case of PDF files, the text has been extracted
 *      (this step may be skipped for files that are not PDF files)</li>
 *  <li>OCR_COMPLETE: files have been OCRed (this step may be skipped for files that 
 *      are not images)</li>
 *  <li>COMPLETE: process complete</li>
 * </ul>
 * @author jdamerow
 *
 */
public enum ProcessingStatus {
    UNPROCESSED,
    AWAITING_STORAGE,
    STORED,
    TEXT_EXTRACTION_COMPLETE,
    IMAGE_EXTRACTION_COMPLETE,
    OCR_COMPLETE,
    COMPLETE
}

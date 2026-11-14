package q1_string;

import java.util.HashMap;
import java.util.Map;

/**
 * Domain model representing a comprehensive Indian Railways (IRCTC-style) E-Ticket
 * comprising exactly 50 distinct metadata, passenger, tariff, and cryptographic fields.
 * 
 * Student: Devi Vara Prasad (Reg: 192365032)
 */
public class ETicket {
    // 1-10: PNR, Train, Station & Schedule Metadata
    public String pnrNumber;
    public String trainNumber;
    public String trainName;
    public String sourceStationCode;
    public String sourceStationName;
    public String destinationStationCode;
    public String destinationStationName;
    public String journeyDate;
    public String scheduledDeparture;
    public String scheduledArrival;

    // 11-20: Class, Quota, Coach & Berth Allotment
    public String travelClass;
    public String quotaCode;
    public String coachNumber;
    public int berthNumber;
    public String berthType;
    public String bookingStatus;
    public String currentStatus;
    public int totalPassengers;
    public String boardingStation;
    public String reservationUpto;

    // 21-30: Passenger Identity, Demographics & Concession
    public String passengerName;
    public int passengerAge;
    public String passengerGender;
    public String idProofType;
    public String idProofNumber;
    public String concessionCategory;
    public String cateringPreference;
    public String mobileNumber;
    public String emailAddress;
    public String nationality;

    // 31-40: Tariff Breakdown, Charges & Taxes
    public double baseFare;
    public double reservationFee;
    public double superfastCharge;
    public double tatkalCharge;
    public double gstAmount;
    public double insurancePremium;
    public double totalFare;
    public String paymentGateway;
    public String transactionId;
    public String paymentTimestamp;

    // 41-50: Operational Telemetry, Security & Diagnostics
    public int distanceKm;
    public int platformNumber;
    public String chartingStatus;
    public String qrCodeHash;
    public String securityDigitalSignature;
    public String agentCode;
    public String clientIpAddress;
    public String terminalId;
    public String cancellationPolicyCode;
    public String remarks;

    public ETicket(int id) {
        // Initialize with realistic mock data
        this.pnrNumber = "PNR" + (1000000000L + id);
        this.trainNumber = "12626";
        this.trainName = "KERALA EXPRESS";
        this.sourceStationCode = "NDLS";
        this.sourceStationName = "NEW DELHI";
        this.destinationStationCode = "TVC";
        this.destinationStationName = "THIRUVANANTHAPURAM CENTRAL";
        this.journeyDate = "2026-12-01";
        this.scheduledDeparture = "20:10";
        this.scheduledArrival = "18:00 +2 days";

        this.travelClass = "3A";
        this.quotaCode = "GN";
        this.coachNumber = "B" + ((id % 12) + 1);
        this.berthNumber = (id % 72) + 1;
        this.berthType = (berthNumber % 8 == 7) ? "SL" : (berthNumber % 8 == 0) ? "SU" : "MB";
        this.bookingStatus = "CNF";
        this.currentStatus = "CNF";
        this.totalPassengers = 1;
        this.boardingStation = "NDLS";
        this.reservationUpto = "TVC";

        this.passengerName = "Passenger_" + id;
        this.passengerAge = 20 + (id % 50);
        this.passengerGender = (id % 2 == 0) ? "MALE" : "FEMALE";
        this.idProofType = "AADHAAR";
        this.idProofNumber = "XXXX-XXXX-" + (1000 + (id % 9000));
        this.concessionCategory = "NONE";
        this.cateringPreference = "VEG";
        this.mobileNumber = "+91-98765" + String.format("%05d", id % 100000);
        this.emailAddress = "passenger" + id + "@railnet.gov.in";
        this.nationality = "INDIAN";

        this.baseFare = 2450.00;
        this.reservationFee = 40.00;
        this.superfastCharge = 45.00;
        this.tatkalCharge = 0.00;
        this.gstAmount = 126.75;
        this.insurancePremium = 0.45;
        this.totalFare = 2662.20;
        this.paymentGateway = "IRCTC_iPay";
        this.transactionId = "TXN" + (900000000000L + id);
        this.paymentTimestamp = "2026-11-14T10:15:30";

        this.distanceKm = 3036;
        this.platformNumber = (id % 16) + 1;
        this.chartingStatus = "CHART NOT PREPARED";
        this.qrCodeHash = "SHA256:7f83b1657ff1fc53b92dc18148a1d65dfc2d4b1fa3d677284addd200126d9069";
        this.securityDigitalSignature = "SIG_RSA2048_" + Integer.toHexString(id * 31);
        this.agentCode = "DIRECT_WEB";
        this.clientIpAddress = "192.168.1." + (id % 254 + 1);
        this.terminalId = "TERM_DELHI_04";
        this.cancellationPolicyCode = "RULE_2026_V2";
        this.remarks = "Happy Journey! Retain SMS/Virtual Ticket with Original ID.";
    }

    /**
     * Extracts all 50 fields as an array of key-value string pairs for uniform generator benchmarking.
     */
    public String[][] getFieldPairs() {
        return new String[][] {
            {"PNR", pnrNumber},
            {"Train No", trainNumber},
            {"Train Name", trainName},
            {"Source Code", sourceStationCode},
            {"Source Name", sourceStationName},
            {"Dest Code", destinationStationCode},
            {"Dest Name", destinationStationName},
            {"Journey Date", journeyDate},
            {"Departure", scheduledDeparture},
            {"Arrival", scheduledArrival},

            {"Class", travelClass},
            {"Quota", quotaCode},
            {"Coach", coachNumber},
            {"Berth No", String.valueOf(berthNumber)},
            {"Berth Type", berthType},
            {"Booking Status", bookingStatus},
            {"Current Status", currentStatus},
            {"Total Passengers", String.valueOf(totalPassengers)},
            {"Boarding Station", boardingStation},
            {"Reservation Upto", reservationUpto},

            {"Passenger Name", passengerName},
            {"Passenger Age", String.valueOf(passengerAge)},
            {"Gender", passengerGender},
            {"ID Type", idProofType},
            {"ID Number", idProofNumber},
            {"Concession", concessionCategory},
            {"Catering", cateringPreference},
            {"Mobile", mobileNumber},
            {"Email", emailAddress},
            {"Nationality", nationality},

            {"Base Fare", String.valueOf(baseFare)},
            {"Reservation Fee", String.valueOf(reservationFee)},
            {"Superfast Charge", String.valueOf(superfastCharge)},
            {"Tatkal Charge", String.valueOf(tatkalCharge)},
            {"GST", String.valueOf(gstAmount)},
            {"Insurance", String.valueOf(insurancePremium)},
            {"Total Fare", String.valueOf(totalFare)},
            {"Payment Gateway", paymentGateway},
            {"Transaction ID", transactionId},
            {"Payment Time", paymentTimestamp},

            {"Distance KM", String.valueOf(distanceKm)},
            {"Platform No", String.valueOf(platformNumber)},
            {"Charting Status", chartingStatus},
            {"QR Hash", qrCodeHash},
            {"Digital Signature", securityDigitalSignature},
            {"Agent Code", agentCode},
            {"Client IP", clientIpAddress},
            {"Terminal ID", terminalId},
            {"Cancellation Code", cancellationPolicyCode},
            {"Remarks", remarks}
        };
    }
}

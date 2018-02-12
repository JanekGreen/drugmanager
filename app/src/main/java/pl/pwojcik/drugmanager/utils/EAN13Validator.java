package pl.pwojcik.drugmanager.utils;

/**
 * @author Tomasz Lubinski
 * www.algorym.org
 * (c) 2005
 *
 * Class for EAN13 validating
 */
public class EAN13Validator {

    private byte EAN13[] = new byte[13];
    private boolean valid = false;

    private static int Numbers[] = {
            0, 10, 11, 12, 13, 2, 30, 31, 32, 33, 34, 35, 36, 37, 380, 383, 385, 387, 40,
            41, 42, 43, 44, 45, 46, 470, 471, 474, 475, 476, 477, 478, 479, 480, 481,
            482, 484, 485, 486, 487, 489, 49, 50, 520, 528, 529, 531, 535, 539, 54, 560,
            569, 57, 590, 594, 599, 600, 601, 608, 609, 611, 613, 619, 621, 622, 624,
            625, 626, 627, 628, 629, 64, 690, 691, 692, 70, 729, 73, 740, 741, 742, 743,
            744, 745, 746, 750, 759, 760, 770, 773, 775, 777, 779, 780, 784, 786, 789,
            790, 80, 81, 82, 83, 84, 850, 858, 859, 860, 867, 869, 87, 880, 885, 888, 890,
            893, 899, 90, 91, 93, 94, 950, 955, 977, 978, 979, 98, 99,
    };

    private static String Countries[] = { "USA i Kanada",
            "USA i Kanada", "USA i Kanada", "USA i Kanada", "USA i Kanada",
            "Do użytku wewnętrznego", "Francja", "Francja", "Francja", "Francja",
            "Francja", "Francja", "Francja", "Francja", "Bułgaria", "Słowenia",
            "Chorwacja", "Bośnia-Hercegowina", "Niemcy", "Niemcy", "Niemcy", "Niemcy",
            "Niemcy", "Japonia", "Rosja", "Kirgistan", "Tajwan", "Estonia", "Łotwa",
            "Azerbejdżan", "Litwa", "Uzbekistan", "Sri Lanka", "Filipiny", "Białoruś",
            "Ukraina", "Mołdawia", "Armenia", "Gruzja", "Kazachstan", "Hong Kong",
            "Japonia", "Wielka Brytania", "Grecja", "Liban", "Cypr", "Macedonia", "Malta",
            "Irlandia", "540-Belgia i Luksemburg", "Portugalia", "Islandia", "Dania",
            "Polska", "Rumunia", "Węgry", "RPA", "RPA", "Bahrain", "Mauritius", "Maroko",
            "Algeria", "Tunezja", "Syria", "Egipt", "Libia", "Jordania", "Iran", "Kuwejt",
            "Arabia Saudyjska", "Emiraty Arabskie", "Finlandia", "Chiny", "Chiny",
            "Chiny", "Norwegia", "Izrael", "730-Szwecja", "Gwatemala", "Salwador",
            "Honduras", "Nikaragua", "Kostaryka", "Panama", "Dominikana", "Meksyk",
            "Wenezuela", "Szwajcaria", "Kolumbia", "Urugwaj", "Peru", "Boliwia",
            "Argentyna", "Chile", "Paragwaj", "Ekwador", "Brazylia", "Brazylia", "Włochy",
            "Włochy", "Włochy", "Włochy", "Hiszpania", "Kuba", "Słowacja", "Czechy",
            "Jugosławia", "Korea Północna", "Turcja", "870-Holandia", "Korea Południowa",
            "Tajlandia", "Singapur", "Indie", "Wietnam", "Indonezja", "Austria",
            "Austria", "Australia", "Nowa Zelandia", "EAN - IDA", "Malezja", "ISSN",
            "ISBN", "ISMN", "Kupony", "Kupony"
    };

    public boolean isValid() {
        return valid;
    }


    public EAN13Validator(String EAN13Number) {
        if (EAN13Number.length() != 13) {
            valid = false;
        }
        else {
            for (int i = 0; i < 13; i++){
                EAN13[i] = Byte.parseByte(EAN13Number.substring(i, i+1));
            }
            if (checkSum()) {
                valid = true;
            }
            else {
                valid = false;
            }
        }
    }

    private boolean checkSum() {
        int sum = 1 * EAN13[0] +
                3 * EAN13[1] +
                1 * EAN13[2] +
                3 * EAN13[3] +
                1 * EAN13[4] +
                3 * EAN13[5] +
                1 * EAN13[6] +
                3 * EAN13[7] +
                1 * EAN13[8] +
                3 * EAN13[9] +
                1 * EAN13[10] +
                3 * EAN13[11];

        sum %= 10;
        sum = 10 - sum;
        sum %= 10;

        if (sum == EAN13[12]) {
            return true;
        }
        else {
            return false;
        }
    }

    public String getCountry() {
        String result = "unknown";
        int code = 0;

        for (int i=0; i<3; i++){

            code *= 10;
            code += EAN13[i];

            for (int j=0; j<127; j++) {
                if (Numbers[j] == code) {
                    result = Countries[j];
                    break;
                }
            }

            if (!result.equals("unknown")) {
                break;
            }

        }

        return result;
    }

}
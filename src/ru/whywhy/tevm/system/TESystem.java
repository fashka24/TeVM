package ru.whywhy.tevm.system;

// System architecture -V
//                     CFD
//             Command Flag Data[]
//               CHR   SFI   1
//               CHS   SFI   0
//               PDC   SFI  123

//              `*C.   1r
//              `mov   r1,  123`

import java.util.HashMap;
import java.util.Map;

public class TESystem {
    final long memsize = 1024;
    long[] memory = new long[(int) memsize];
    long[][] regs = new long[8][8];
    // +++++++++++++++++++++++++
    long
        cell = 0,
        indX = 0,
        indY = 0,
        cursor = 0;

    //?? <<<<<<<<<<<<<
    private static DotaConstruct DC(long a) {
        return new DotaConstruct(a);
    }

    public TESystem() {
    }

    final static Map<DotaConstruct, String> regs_log = new HashMap<>() {{
        put(DC(0), "r0");
        put(DC(1), "r1");
        put(DC(2), "r2");
        put(DC(3), "r3");
        put(DC(4), "r4");
        put(DC(5), "r5");
        put(DC(6), "r6");
        put(DC(7), "r7");
    }};
    final static Map<String, DotaConstruct> regs_alog = new HashMap<>() {{
        put("r0", DC(0));
        put("r1", DC(1));
        put("r2", DC(2));
        put("r3", DC(3));
        put("r4", DC(4));
        put("r5", DC(5));
        put("r6", DC(6));
        put("r7", DC(7));
//        put("r0", DC(0));
    }};

    final static long SFI = 1;
    final static long SFA = 2;

    final static long CHR = 1;
    final static long CHS = 2;
    final static long PDN = 3;
    final static long PDC = 4;

    final static long SIGEND = 3375;
  /*
  <================Flag types===================>
     *   SFI === STD flag
     *   SFA =B= STD address flag
  <==============Command types==================>
     *   CHR === Change Register
     *   CHS === Change current (Register) Section
     *   PDN === Put Data (to) Null rsn(register section)
     *   PDC === Put Data (to) Current rsn(register section)
  <=============================================>
    */

    /* 8 regs
    *  r0 - 0
    *  r1 - 1
    *  ...
    */
    private boolean isSFI(int a) {
        return memory[(int) cell + a] == SFI;
    }
    public void Configure() {
        cursor = 0;;
    }
    public void start() {
        Configure();

        // CODE {
        CHR_();
            SFI_();
            DATA_(regs_alog.get("r1").getSOURCE());
        CHS_();
            SFI_();
            DATA_(0);
        PDC_();
            SFI_();
            DATA_(123);
        MOV_("r2", 1, 555);
        END_();
        // }

        interpret();
    }

    private void interpret() {
        while (memory[(int) cell] != SIGEND) {
            if (memory[(int) cell] == CHR
                    && isSFI(1)) {
                indX = memory[(int) cell + 2];
                cell++;
            }
            else if (memory[(int) cell] == CHS
                    && isSFI(1)) {
                indY = memory[(int) cell + 2];
                cell++;
            }
            else if (memory[(int) cell] == PDN
                    && isSFI(1)) {
                regs[(int) indX][0] = memory[(int) cell + 2];
                cell++;
            }
            else if (memory[(int) cell] == PDC
                    && isSFI(1)) {
                regs[(int) indX][(int) indY] = memory[(int) cell + 2];
                cell++;
            }

            cell++;
        }
    }

    private void CHR_() {
        {memory[(int) cursor++] = CHR;}
    }
    private void CHS_() {
        {memory[(int) cursor++] = CHS;}
    }
    private void PDN_() {
        {memory[(int) cursor++] = PDN;}
    }
    private void PDC_() {
        {memory[(int) cursor++] = PDC;}
    }

    private void SFI_() {
        {memory[(int) cursor++] = SFI;}
    }
    private void SFA_() {
        {memory[(int) cursor++] = SFA;}
    }

    private void DATA_(long dat) {
        {memory[(int) cursor++] = dat;}
    }
    private void MOV_(String reg, long section, long dat) {
        CHR_();
            SFI_();
            DATA_(regs_alog.get(reg).getSOURCE());
        CHS_();
            SFI_();
            DATA_(section);
        PDC_();
            SFI_();
            DATA_(dat);
    }
    private void END_() {
        {memory[(int) cursor++] = SIGEND;}
    }

    public void print_regs() {
        for (int i = 0; i < 8; i++) {
            System.out.print(i + " - [ ");
            for (int j = 0; j < 8; j++) {
                System.out.print(this.regs[i][j] + " ");
            }
            System.out.println("]");
        }
    }
}

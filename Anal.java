/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

/**
 *
 * @author Lyndon
 */
import java.io.*;
import static java.lang.Character.*;
import java.util.*;
public class Anal extends LALR{
    boolean stopP = false;
    Token tk;
    File f;
    BufferedInputStream tokenReader;
    BufferedInputStream tR;
    FileInputStream s;
    PushbackInputStream ll;
//    PushbackInputStream t;
//    PushbackInputStream sll;
    //PositionedInputStream pl;
    int ctr = 1;
    static HashMap<String, Token> reservedWord = new HashMap();
    static HashMap<String, Token> idWord = new HashMap();
    static Scanner console = new Scanner(System.in);
    public void reader()throws Exception{
        int p;
        s = new FileInputStream("src/emp1.txt");
        //t = new BufferedInputStream(s);
        tokenReader = new BufferedInputStream(s);
        tR = tokenReader;
        tR.mark(0);
        ll = new PushbackInputStream(tokenReader);
        tokenReader.mark(0);
        getToken();
        p = ll.read();
    }
    public boolean isOperator(char c){
        String s = "+-*/";
        String a = "" + c;
        if(s.contains(a)) return true;
        else return false;
    }
    public void getToken() throws Exception{
        //PushbackInputStream ll1 = ll;
        //int state = 1; 
        int p;
        //char c;
        Token tok;
        tokenReader.mark(0);
        p = ll.read();
        while(((char)p) != '¥' && p != -1 /*&& stopP != true*/){
            ll.unread(p);
            whiteSpace();
            String s = "" + (char)p;
            if(p == '\r' && p == '\n'){
                //System.out.println("new line");
                ctr++;
                System.out.println(ctr);
                
            }
            if((char) p == '$'){
                //System.out.println(getID().getInfo());
                tok = getID();
            }
            else if((char) p == ':' || (char) p == '#'){
                tok = getComments();
                //System.out.println(getComments().getInfo());
            }
            else if(isLetter((char)p)){
                //System.out.println(checkResWord().getInfo());
                tok = checkResWord();
            }
            else if(isDigit((char) p)){
                //System.out.println(getNumConst().getInfo());
                tok = getNumConst();
            }
            else if((char) p == '"'){
                //System.out.println(stringConst().getInfo());
                tok = stringConst();
            }
            else if((char) p == '>' || (char) p == '<'){
                //System.out.println(checkRelop().getInfo());
                tok = checkRelop();
            }
            else if(isOperator((char) p)){
                //System.out.println(checkArith().getInfo());
                tok = checkArith();
            }
            else if((char) p == ';'){
                //System.out.println(checkForTerminator().getInfo());
                tok = checkForTerminator();
            }
            else if((char) p == ']' || (char) p == ')' || (char) p == '}'){
                //System.out.println(checkForRight().getInfo());
                tok = checkForRight();
            }
            else if((char) p =='(' || (char) p == '{' || (char) p == '['){
                System.out.println(checkForLeft().getInfo());
            }
            
            /*if(stopP){
                System.out.println("ERROR at LINE: " + ctr + " ERROR: " + tk.getName() + " = UKNOWN SYNTAX ERROR");
                stopP = false;
            }*/
                //System.out.println("newline daw");
            p = ll.read();
        }
    }
    public Token checkArith() throws Exception{
        PushbackInputStream ll1 = ll;
        int state = 1; 
        int p = 0;
        char c;
        String builder = "";
        while(true){
            switch(state){
                case 1:
                    p = ll1.read();
                    c = (char) p;
                    if(c == '+'){
                        state = 2;
                        builder = builder + c;
                    }
                    else if(c == '-'){
                        state = 3;
                        builder = builder + c;
                    }
                    else if(c == '*'){
                        state = 4;
                        builder = builder + c;
                    }
                    else if(c == '/'){
                        state = 5; 
                        builder = builder + c;
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token();
                    }
                    break;
                case 2:
                    ll = ll1;
                    return new Token("+", TokenKind.ArithAS, TokenType.RESERVED);
                case 3:
                    ll = ll1;
                    return new Token("-", TokenKind.ArithAS, TokenType.RESERVED);
                case 4:
                    ll = ll1;
                    return new Token("*", TokenKind.ArithMD, TokenType.RESERVED);
                case 5:
                    ll = ll1;
                    return new Token("/", TokenKind.ArithMD, TokenType.RESERVED);
            }
        }
    }
    public Token stringConst() throws Exception{
        PushbackInputStream ll1 = ll;
        int state = 1; 
        int p = 0;
        char c;
        String builder = "";
        while(true){
            switch(state){
                case 1:
                    p = ll1.read();
                    c = (char) p;
                    if(c == '"') state = 2;
                    else state = 4;
                    break;
                case 2:
                    p = ll1.read();
                    c = (char) p;
                    char line = (char) 10;
                    char tab = (char) 9;
                    if(p == 64){
                        p = ll1.read();
                        c = (char) p;
                        if(c == 'n'){
                            builder = builder + line;
                        }
                        else if(c == 't'){
                            builder = builder + tab;
                        }
                        else if(c == '"') builder = builder + c;
                        state = 2; 
                    }
                    else if((p < 34 && p > 31) || (p < 127 && p > 34) && p != 92){
                        state = 2;
                        builder = builder + c;
                    }
                    else{
                        ll.unread(p);
                        state = 3;
                    }
                    break;
                case 3:
                    p = ll1.read();
                    c = (char) p;
                    if(c == '"') return new Token(builder, TokenKind.string_format, TokenType.STRING_CONST);
                    else state = 4;
                    break;
                case 4:
                    ll1.unread(p);
                    ll = ll1;
                    stopP = true;
                    return new Token("ERROR AT LINE: " + ctr + " UNKNOWN STRING FORMAT", TokenKind.ERROR, TokenType.ERROR);
            }
        }
    }
    public Token getID() throws Exception{
        PushbackInputStream ll1 = ll;
        int state = 1; 
        int p = 0;
        char c;
        String builder = "";
        while(true){
            switch(state){
                case 1:
                    p = ll1.read();
                    c = (char) p;
                    if(c == '$'){
                        state = 2;
                        builder = builder + c;
                    }
                    else{
                        state = 20;
                    }
                    break;
                case 2: 
                    p = ll1.read();
                    c = (char) p;
                    if(isLetter(c)){
                        builder = builder + c;
                        state = 3;
                    }
                    else state = 20;
                    break;
                case 3:
                    p = ll1.read();
                    c = (char) p;
                    if(isLetter(c) || isDigit(c)){
                        builder = builder + c;
                        state = 3;
                    }
                    else if(isWhitespace(c)){
                        state = 4;
                        
                    } 
                    else if(c == ';') state = 4;
                    else{
                        
                        state = 4;
                    }
                    break;
                case 4:
                    ll1.unread(p);
                    ll = ll1;
                    return new Token(builder,TokenKind.identifier,TokenType.ID);
                case 20: 
                    stopP = true;
                    return new Token("ERROR AT LINE: " + ctr + " UNKNOWN ID FORMAT", TokenKind.ERROR, TokenType.ERROR);
            }
        }
    }
    public Token checkForTerminator() throws Exception{
        PushbackInputStream ll1 = ll;
        int flag=0; // marker for plant & fertilize
        int state = 1; 
        int p = 0;
        char c;
        String builder = "";
        while(true){
            switch(state){
                case 1:
                    p = ll1.read();
                    c = (char) p;
                    if(c == ';'){
                        builder = builder + c;
                        state = 2;
                        break;
                    }
                    else{
                        state = 3;
                        break;
                    }
                case 2:
                    ll = ll1;
                    return new Token(builder, TokenKind.terminator, TokenType.EOF);
                case 3:
                    ll = ll1;
                    stopP = true;
                    return new Token(builder, TokenKind.ERROR, TokenType.ERROR);
            }
        }
    }
    
    public Token checkResWord() throws Exception {
        PushbackInputStream ll1 = ll;
        int flag=0; // marker for plant & fertilize
        int state = 1; 
        int p = 0;
        char c;
        String builder = "";
        while(true)
        {
            switch(state)
            {
                case 1:
                    p = ll1.read();
                    c = (char)p;
                    if (c == 'S') //SUNRISE/SUNSET/SEASON
                    {   
                        state = 2; 
                        builder = builder + c;
                    }
                    else if (c == 'F') //FARMIN / FARMOUT / FERTILIZE** 
                    {
                        state = 12;
                        builder = builder + c;
                    }
                    else if (c == 'P') //PLANT**/PLOW
                    {
                        state = 28;
                        builder = builder + c;
                    }
                    else if (c == 'I') // IF
                    {
                        state = 32;
                        builder = builder + c;
                    }
                    else if (c == 'E') // ELSE / ELSEIF / ENDIF
                    {
                        state = 34;
                        builder = builder + c;
                    }
                    else if (c == 'T') // THEN/TRIM
                    {
                        state = 40;
                        builder = builder + c;
                    }
                    else if (c == 'H') // HARVEST
                    {
                        state = 44;
                        builder = builder + c;
                    }
                    else if (c=='W')
                    {
                        state=58;
                        builder = builder+c;
                    }
                    else if (c == 'G') //GROW
                    {
                        state = 51;
                        builder = builder + c;
                    }
                    else if (c=='C')//CHANGE
                    {
                        state = 63;
                        builder = builder + c;
                    }
                    else if (c=='A') // AND
                    {
                        state = 104;
                        builder = builder+c;
                    }
                    else if(c=='O') // OR
                    {
                        state=107;
                        builder = builder+c;
                    }
                    else if (c=='N') // NOT/NO
                    {
                        state = 109;
                        builder=builder+c;
                    }
                    else if (c=='Y')//YES
                    {
                        state=112;
                        builder=builder+c;
                    }
                    else if (c==';')
                    {
                        state = 63;
                        builder = builder + c;
                    }
                    else state = 99; //error state
                    break;
                case 2:
                    p = ll1.read();
                    c = (char)p;
                    if (c == 'U')
                    {
                        state = 3;
                        builder = builder + c; //SU
                    }
                    else if (c == 'E') //SE
                    {
                        state = 69;
                        builder = builder + c;
                    }
                    else state = 99;
                    break;
                case 3:
                    p = ll1.read();
                    c = (char)p;
                    if (c == 'N')
                    {
                        state = 4;
                        builder = builder + c; //SUN*
                    }
                    else state = 99;
                    break;
                case 4:
                    p = ll1.read();
                    c = (char)p;
                    if (c == 'R')
                    {
                        state = 5;
                        builder = builder + c; //SUNR
                    }
                    else if (c == 'S')
                    {
                        state = 9;
                        builder = builder + c; //SUNS
                    }
                    else state = 99;
                    break;
                case 5:
                    p = ll1.read();
                    c = (char)p;
                    if (c == 'I')
                    {
                        state = 6;
                        builder = builder + c; //SUNRI
                    }
                    else state = 99;
                    break;
                case 6:
                    p = ll1.read();
                    c = (char)p;
                    if (c == 'S')
                    {
                        state = 7;
                        builder = builder + c; //SUNRIS
                    }
                    else state = 99;
                    break;
                case 7:
                    p = ll1.read();
                    c = (char)p;
                    if (c == 'E')
                    {
                        state = 8;
                        builder = builder + c; // SUNRISE
                    }
                    break;
                case 8:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.start,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.start,TokenType.RESERVED);
                    }
                    //reak;
                case 9:
                    p = ll1.read();
                    c = (char)p;
                    if (c == 'E')
                    {
                        state = 10;
                        builder = builder + c; //SUNSE
                    }
                    else state = 99;
                    break;
                case 10:
                    p = ll1.read();
                    c = (char)p;
                    if (c == 'T')
                    {
                        state = 11;
                        builder = builder + c; //SUNSET
                    }
                    else state = 99;
                    break;
                case 11:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c))
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.end,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.end,TokenType.RESERVED);
                    }
                    
                case 12:
                    p = ll1.read();
                    c = (char)p;
                    if (c=='A')
                    {
                        state = 13;
                        builder = builder + c; //FA
                    }
                    else if (c=='E')
                    {
                        state = 21;
                        builder = builder + c; //FE
                    }
                    else state = 99;
                    break;
                case 13:
                    p = ll1.read();
                    c = (char)p;
                    if(c == 'R')
                    {
                        state = 14;
                        builder = builder + c; //FAR
                    }
                    else 
                        state = 99;
                    break;
                case 14:
                    p = ll1.read();
                    c = (char)p;
                    if (c=='M')
                    {
                        state = 15;
                        builder = builder + c; //FARM*
                    }
                    else state = 99;
                    break;
                case 15:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='I')
                    {
                        state = 16;
                        builder = builder + c; //FARMI
                    }
                    else if (c=='O')
                    {
                        state = 18;
                        builder = builder + c; //FARMO
                    }
                    else state = 99;
                    break;
                case 16:
                    p = ll1.read();
                    c = (char)p;
                    if (c=='N')
                    {
                        state = 17;
                        builder = builder + c; //FARMIN
                    }
                    else state =99;
                    break;
                case 17:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.start_main,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.start_main,TokenType.RESERVED);
                    }
                case 18:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='U')
                    {
                        state = 19;
                        builder = builder + c; //FARMOU
                    }
                    else state = 99;
                    break;
                case 19:
                    p = ll1.read();
                    c = (char)p;
                    if (c=='T')
                    {
                        state = 20;
                        builder = builder + c; //FARMOUT
                    }
                    else state = 99;
                    break;
                case 20:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.end_main,TokenType.RESERVED);
                    }
                    else state = 99;
                    break;
                case 21:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='R')
                    {
                        state = 22;
                        builder = builder + c; //FER
                    }
                    else state = 99;
                    break;
                case 22:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='T')
                    {
                        state=23;
                        builder = builder + c; //FERT
                    }
                    else state=99;
                    break;
                case 23:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='I')
                    {
                        state = 24;
                        builder = builder + c;//FERTI
                    }
                    else state = 99;
                    break;
                case 24:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='L')
                    {
                        state = 25;
                        builder = builder + c;//FERTIL
                    }
                    else state = 99;
                    break;
                case 25:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='I')
                    {
                        state = 26;
                        builder = builder+c;//FERTILI
                    }
                    else state = 99;
                    break;
                case 26:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='Z')
                    {
                        state=27;
                        builder = builder+c;//FERTILIZ
                    }
                    else state=99;
                    break;
                case 27:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='E')
                    {
                        flag=1;
                        state = 74;
                        builder = builder+c;//FERTILIZE*
                    }
                    else state =99;
                    break;
                case 28:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='L')
                    {
                        state = 29;
                        builder = builder+c;//PL*
                    }
                    else state =99; 
                    break;
                case 29:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='A')
                    {
                        state = 30;
                        builder = builder+c;//PLA
                    }
                    else if (c=='O')
                    {
                        state = 91;
                        builder = builder+c;//PLO
                    }
                    else state =99; 
                    break;
                case 30:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='N')
                    {
                        state = 31;
                        builder = builder+c;//PLAN
                    }
                    else state =99;
                    break;
                case 31:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='T')
                    {
                        flag=2;
                        state = 74;
                        builder = builder+c;//PLANT
                    }
                    else state =99; 
                    break;
                case 32:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='F')
                    {
                        state = 33;
                        builder = builder+c;//IF
                    }
                    else if(c=='S')
                    {
                        state = 94;
                        builder = builder+c;//IS
                    }
                    else state =99; 
                    break;
                case 33:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.cond_if,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.cond_if,TokenType.RESERVED);
                    }
                case 34:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='L')
                    {
                        state = 35;
                        builder = builder+c;//EL
                    }
                    else if(c=='N')
                    {
                        state=115;
                        builder=builder+c; //EN
                    }
                    else state =99;
                    break;
                case 35:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='S')
                    {
                        state = 36;
                        builder = builder+c;//ELS
                    }
                    else state =99; 
                    break;
                case 36:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='E')
                    {
                        state = 37;
                        builder = builder+c;//ELSE
                    }
                    else state =99;
                    break;
                case 37:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='I')
                    {
                        state=38;
                        builder=builder+c;//ELSEI
                    }
                    else if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.cond_else,TokenType.RESERVED);
                    }    
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.cond_else,TokenType.RESERVED);
                    }
                    break;
                case 38:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='F')
                    {
                        state=39;
                        builder=builder+c;//ELSEIF
                    }
                    else state=99;
                    break;
                case 39:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.cond_elseif,TokenType.RESERVED);
                    }    
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.cond_elseif,TokenType.RESERVED);
                    }
                case 40:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='H')
                    {
                        state=41;
                        builder=builder+c;//TH
                    }
                    else if(c=='R')
                    {
                        state = 55;
                        builder=builder+c;//TR
                    }
                    else state=99;
                    break;
                case 41:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='E')
                    {
                        state=42;
                        builder=builder+c;//THE
                    }
                    else state=99;
                    break;
                case 42:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='N')
                    {
                        state=43;
                        builder=builder+c;//THEN
                    }
                    else state=99;
                    break;
                case 43:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.cond_then,TokenType.RESERVED);
                    }    
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.cond_then,TokenType.RESERVED);
                    }
                case 44:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='A')
                    {
                        state=45;
                        builder=builder+c;//HA
                    }
                    else state=99;
                    break;
                case 45:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='R')
                    {
                        state=46;
                        builder=builder+c;//HAR
                    }
                    else state=99;
                    break;
                case 46:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='V')
                    {
                        state=47;
                        builder=builder+c;//HARV
                    }
                    else state=99;
                    break;
                case 47:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='E')
                    {
                        state=48;
                        builder=builder+c;//HARVE
                    }
                    else state=99;
                    break;
                case 48:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='S')
                    {
                        state=49;
                        builder=builder+c;//HARVES
                    }
                    else state=99;
                    break;
                case 49:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='T')
                    {
                        state=50;
                        builder=builder+c;//HARVEST
                    }
                    else state=99;
                    break;
                case 50:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.output_type,TokenType.RESERVED);
                    }    
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.output_type,TokenType.RESERVED);
                    }
                case 51:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='R')
                    {
                        state=52;
                        builder=builder+c;//GR
                    }
                    else state=99;
                    break;
                case 52:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='O')
                    {
                        state=53;
                        builder=builder+c;//GRO
                    }
                    else state=99;
                    break;
                case 53:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='W')
                    {
                        state=54;
                        builder=builder+c;//GROW
                    }
                    else state=99;
                    break;
                case 54:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.increment,TokenType.RESERVED);
                    }    
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.increment,TokenType.RESERVED);
                    }
                case 55:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='I')
                    {
                        state=56;
                        builder=builder+c;//TRI
                    }
                    else state=99;
                    break;
                case 56:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='M')
                    {
                        state=57;
                        builder=builder+c;//TRIM
                    }
                    else state=99;
                    break;
                case 57:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.decrement,TokenType.RESERVED);
                    }    
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.decrement,TokenType.RESERVED);
                    }
                case 58:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='H')
                    {
                        state=59;
                        builder=builder+c;//WH
                    }
                    else state=99;
                    break;
                case 59:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='I')
                    {
                        state=60;
                        builder=builder+c;//WHI
                    }
                    else state=99;
                    break;
                case 60:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='L')
                    {
                        state=61;
                        builder=builder+c;//WHIL
                    }
                    else state=99;
                    break;
                case 61:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='E')
                    {
                        state=62;
                        builder=builder+c;//WHILE
                    }
                    else state=99;
                    break;
                case 62:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.loop_while,TokenType.RESERVED);
                    }    
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.loop_while,TokenType.RESERVED);
                    }
                case 63:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='H')
                    {
                        state=64;
                        builder=builder+c;//CH
                    }
                    else state=99;
                    break;
                case 64:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='A')
                    {
                        state=65;
                        builder=builder+c;//CHA
                    }
                    else state=99;
                    break;
                case 65:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='N')
                    {
                        state=66;
                        builder=builder+c;//CHAN
                    }
                    else state=99;
                    break;
                case 66:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='G')
                    {
                        state=67;
                        builder=builder+c;//CHANG
                    }
                    else state=99;
                    break;
                case 67:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='E')
                    {
                        state=68;
                        builder=builder+c;//CHANGE
                    }
                    else state=99;
                    break;
                   
                case 68:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.switch_loop,TokenType.RESERVED);
                    }    
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.switch_loop,TokenType.RESERVED);
                    }
                case 69:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='A')
                    {
                        state=70;
                        builder=builder+c;//SEA
                    }
                    else state=99;
                    break;
                case 70:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='S')
                    {
                        state=71;
                        builder=builder+c;//SEAS
                    }
                    else state=99;
                    break;
                case 71:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='O')
                    {
                        state=72;
                        builder=builder+c;//SEASO
                    }
                    else state=99;
                    break;
                case 72:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='N')
                    {
                        state=73;
                        builder=builder+c;//SEASON
                    }
                    else state=99;
                    break;
                case 73:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.switch_loop,TokenType.RESERVED);
                    }    
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.switch_loop,TokenType.RESERVED);
                    }
                case 74:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='I')//int
                    {
                        state = 75;
                        builder=builder+c;//FERTILIZEI
                    }
                    else if(c=='D')//dec
                    {
                        state=78;
                        builder=builder+c;//FERTILIZED
                    }
                    else if(c=='S')//string
                    {
                        state=81;
                        builder=builder+c;//FERTILIZES
                    }
                    else if(c=='B')//bool
                    {
                        state=87;
                        builder=builder+c;//FERTILIZEB
                    }
                    else state=99;
                    break;
                case 75:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='N')
                    {
                        state=76;
                        builder=builder+c;//FERTILIZEIN
                    }
                    else state=99;
                    break;
                case 76:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='T')
                    {
                        state=77;
                        builder=builder+c;//FERTILIZEINT
                    }
                    else state=99;
                    break;
                case 77:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c) && flag==1) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.input_type,TokenType.RESERVED);
                    }
                    else if(isWhitespace(c) && flag==2)
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.data_type_int,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.data_type_int,TokenType.RESERVED);
                    }
                    //break;
                case 78:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='E')
                    {
                        state = 79;
                        builder=builder+c;//FERTILIZEDE
                    }
                    else state=99;
                    break;
                case 79:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='C')
                    {
                        state=80;
                        builder=builder+c;//FERTILIZEDEC
                    }
                    else state=99;
                    break;
                case 80:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c) && flag==1) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.input_type,TokenType.RESERVED);
                    }
                    else if(isWhitespace(c) && flag==2) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.data_type_double,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.data_type_int,TokenType.RESERVED);
                    }
                case 81:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='T')
                    {
                        state = 82;
                        builder = builder+c;//FERTILIZEST
                    }
                    else state=99;
                    break;
                case 82:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='R')
                    {
                        state=83;
                        builder=builder+c;//FERTILIZESTR
                    }
                    else state=99;
                    break;
                case 83:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='I')
                    {
                        state=84;
                        builder=builder+c;//FERTILIZESTRI
                    }
                    else state=99;
                    break;
                case 84:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='N')
                    {
                        state=85;
                        builder=builder+c;//FERTILIZESTRIN
                    }
                    else state=99;
                    break;
                case 85:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='G')
                    {
                        state=86;
                        builder=builder+c;//FERTILIZESTRING
                    }
                    else state=99;
                    break;
                case 86:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)&&flag==1) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.input_type,TokenType.RESERVED);
                    }
                    else if(isWhitespace(c)&&flag==2) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.data_type_string,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.data_type_string,TokenType.RESERVED);
                    }
                case 87:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='O')
                    {
                        state=88;
                        builder=builder+c;//FERTILIZEBO
                    }
                    else state=99;
                    break;
                case 88:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='O')
                    {
                        state=89;
                        builder=builder+c;//FERTILIZEBOO
                    }
                    else state=99;
                    break;
                case 89:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='L')
                    {
                        state=90;
                        builder=builder+c;//FERTILIZEBOOL
                    }
                    else state=99;
                    break;
                case 90:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)&&flag==1) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.input_type,TokenType.RESERVED);
                    }
                    else if(isWhitespace(c)&&flag==2) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.data_type_bool,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.data_type_bool,TokenType.RESERVED);
                    }
                case 91:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='W')
                    {
                        state = 92;
                        builder = builder+c;//PLOW
                    }
                    else state =99; 
                    break;
                case 92:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.loop_do,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.loop_do,TokenType.RESERVED);
                    }
                case 93:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(";",TokenKind.terminator,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.terminator,TokenType.RESERVED);
                    }
                case 94:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.assign_Op,TokenType.RESERVED);
                    }
                    else if(c=='E')
                    {
                        state=95;
                        builder  = builder+c; //ISE
                        
                    }
                    else if (c=='N')
                    {
                        state=101;
                        builder = builder+c; //ISN
                        
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.assign_Op,TokenType.RESERVED);
                    }
                case 95:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='Q')
                    {
                        state = 96;
                        builder=builder+c;//ISEQ
                    }
                    else state = 99;
                    break;
                case 96:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='U')
                    {
                        state = 97;
                        builder=builder+c; //ISEQU
                    }
                    break;
                case 97:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='A')
                    {
                        state = 98;
                        builder=builder+c; //ISEQUA
                    }
                    break;
                case 98:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='L')
                    {
                        state=100;
                        builder=builder+c;
                    }
                    break;
                case 99: //ERROR STATE
                    stopP = true;
                    tk = new Token("ERROR",TokenKind.ERROR,TokenType.ERROR); 
                    return new Token("ERROR AT LINE: " + ctr + " UNKNOWN RESERVED WORD", TokenKind.ERROR, TokenType.ERROR);
                case 100:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.relop,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.relop,TokenType.RESERVED);
                    }
                case 101: //NOT
                    p = ll1.read();
                    c = (char)p;
                    if(c=='O')
                    {
                        state=102;
                        builder=builder+c;//ISNO
                    }
                    else state = 99;
                    break;
                case 102:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='T')
                    {
                        state=103;
                        builder=builder+c;//ISNOT
                    }
                    else state = 99;
                    break;
                case 103:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='E')
                    {
                        state = 95;
                        builder=builder+c;//ISNOTE
                    }
                    else state = 99;
                    break;
                case 104:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='N')
                    {
                        state = 105;
                        builder=builder+c;//AN
                    }
                    else state = 99;
                    break;
                case 105:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='D')
                    {
                        state = 106;
                        builder=builder+c;//AND
                    }
                    else state = 99;
                    break;
                case 106:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.log_Optr,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.log_Optr,TokenType.RESERVED);
                    }
                case 107:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='R')
                    {
                        state = 108;
                        builder=builder+c;//OR
                    }
                    else state = 99;
                    break;
                case 108:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.log_Optr,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.log_Optr,TokenType.RESERVED);
                    }
                case 109:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='O')
                    {
                        state = 110;
                        builder=builder+c;//NO*
                    }
                    else state = 99;
                    break;
                case 110:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='T')
                    {
                        state = 111;
                        builder=builder+c;//NOT
                    }
                    else if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.bool_const,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.bool_const,TokenType.RESERVED);
                    }
                case 111:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.log_Optr,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.log_Optr,TokenType.RESERVED);
                    }
                case 112:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='E')
                    {
                        state = 113;
                        builder=builder+c;//YE
                    }
                    else state = 99;
                    break;
                case 113:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='S')
                    {
                        state = 114;
                        builder=builder+c;//YES
                    }
                    else state = 99;
                    break;
                case 114:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.bool_const,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.bool_const,TokenType.RESERVED);
                    }
                case 115:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='D')
                    {
                        state = 116;
                        builder=builder+c;//END
                    }
                    else state = 99;
                    break;
                case 116:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='I')
                    {
                        state = 117;
                        builder=builder+c;//ENDI
                    }
                    else state = 99;
                    break;
                case 117:
                    p = ll1.read();
                    c = (char)p;
                    if(c=='F')
                    {
                        state = 118;
                        builder=builder+c;//ENDIF
                    }
                    else state = 99;
                    break;
                case 118:
                    p = ll1.read();
                    c = (char)p;
                    if(isWhitespace(c)) 
                    {
                        ll = ll1;
                        return new Token(builder,TokenKind.cond_stmt,TokenType.RESERVED);
                    }
                    else{
                        ll1.unread(p);
                        ll = ll1;
                        return new Token(builder, TokenKind.cond_stmt,TokenType.RESERVED);
                    }
            }
        }      
    }    
    
    public Token checkRelop() throws Exception{
        PushbackInputStream ll1 = ll;
        int state = 1; 
        int p = 0;
        char c;
        String builder = "";
        while(true){
            switch(state){
                case 1:
                    p = ll1.read();
                    c = (char) p;
                    //if(c == 'I') state = 2;
                    if(c == '<'){
                        state = 11;
                        builder = builder + c;
                    }
                    else if(c == '>'){
                        builder = builder + c;
                        state = 11;
                    }
                    //else if(c == 'N') state = 4;
                    else state = 20;
                    break;
                /*case 2:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'S') state = 3;
                    else state = 20;
                    break;
                case 3:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'N') state = 4;
                    else if (c == 'E') state = 6;
                    else state = 20;
                    break;
                case 4: 
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'O') state = 5;
                    else state = 20;
                    break;
                case 5:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'T') state = 6;
                    
                    else state = 20;
                    break;
                case 6: 
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'E') state = 7;
                    else if(isWhitespace(c)) state = 18;
                    else state = 20;
                    break;
                case 7:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'Q') state = 8;
                    else state = 20;
                    break;
                case 8:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'U') state = 9;
                    else state = 20;
                    break;
                case 9:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'A') state = 10;
                    else state = 20;
                    break;
                case 10:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'L') state = 19;*/
                case 11:
                    p = ll1.read();
                    c = (char) p;
                    if(c == '='){
                        builder = builder + c;
                        state = 17;
                    } 
                    else if(isWhitespace(c)) state = 17;
                    else state = 18;
                case 17:
                    //ll1.unread(p);
                    ll = ll1;
                    return new Token(builder, TokenKind.relop, TokenType.RELOP);
                case 18: 
                    ll1.unread(p);
                    ll = ll1;
                    return new Token(builder, TokenKind.relop, TokenType.RELOP);
                case 19:
                    ll = ll1;
                    return new Token(/**/);
                case 20:
                    ll = ll1; 
                    return new Token("ERROR AT LINE: " + ctr + " UNKNOWN RELOP FORMAT", TokenKind.ERROR, TokenType.ERROR);
            }
        }
    }
    public Token checkAss() throws Exception{
        PushbackInputStream ll1 = ll;
        int state = 1; 
        int p = 0;
        char c;
        String builder = "";
        while(true)
            switch(state){
                case 1:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'I') state = 2;
                    else state = 3;
                    break;
                case 2: 
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'S') state = 4;
                    else state = 3;
                    break;
                case 3:
                    return new Token(/**/);
                case 4:
                    p = ll1.read();
                    c = (char) p;
                    if(c == ' ') return new Token(/**/);
            }
    }
    public void whiteSpace() throws Exception{
        PushbackInputStream ll1 = ll;
        int state = 1; 
        int p = 0;
        char c;
        String builder = "";
        p = ll1.read();
        c = (char) p;
        //boolean totalWhite = true;
        while (isWhitespace(c)) {
            p = ll1.read();
            c = (char) p;
            //builder = builder + c;
            //break Outerloop;
        }
        ll1.unread(p);
        ll = ll1;
        //return new Token(/*"WHITESPACE", */);
    }
    
    /*public Token checkAssLogOptr() throws Exception{
        PushbackInputStream ll1 = ll;
        int state = 1; 
        int p = 0;
        char c;
        String builder = "";
        while(true)
            switch(state){
                case 1:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'I') state = 2;
                    else if(c == 'N') state = 4;
                    else state = 20;
                    break;
                case 2:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'S') state = 3;
                    else state = 20;
                    break;
                case 3:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'N') state = 4;
                    else if (c == 'E') state = 6;
                    else if (c == 'O') state = 5;
                    else state = 20;
                    break;
                case 4: 
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'O') state = 5;
                    else state = 20;
                    break;
                case 5:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'T') state = 6;
                    else if(isWhitespace(c)) state = 17;
                    else state = 20;
                    break;
                case 6: 
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'E') state = 7;
                    else if(isWhitespace(c)) state = 18;
                    else state = 20;
                    break;
                case 7:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'Q') state = 8;
                    else state = 20;
                    break;
                case 8:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'U') state = 9;
                    else state = 20;
                    break;
                case 9:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'A') state = 10;
                    else state = 20;
                    break;
                case 10:
                    p = ll1.read();
                    c = (char) p;
                    if(c == 'L') state = 19;
                case 11:
                    p = ll1.read();
                    c = (char) p;
                    if(c == '=') state = 17; 
                    else if(isWhitespace(c)) state = 17;
                    else state = 20;
                case 17:
                    ll = ll1;
                    return new Token(/*RELOP);
                case 18: 
                    ll = ll1;
                    return new Token(/*);
                case 19:
                    ll = ll1;
                    return new Token(/*);
                case 20:
                    ll = ll1; 
                    return new Token(/*);
            }
            
//        return null;
    }*/
    public Token getNumConst() throws Exception{
        PushbackInputStream ll1 = ll;
        int state = 1; 
        int p = 0;
        char c;
        String builder = "";
        while(true){
            switch(state){
                case 1:
                    p = ll1.read();
                    c = (char) p;
                    //System.out.println(c);
                    if(p == 48){
                        builder = builder + c;
                        p = ll1.read();
                        c = (char) p;
                        if((char) p == ';'){
                            ll1.unread(p);
                            ll = ll1;
                            state = 4;
                        }
                        else{
                            state = 3;
                            builder = builder + c;
                        }
                        
                    }
                    else{
                        builder = builder + c;
                        state = 2;
                    }
                    break;
                case 2:
                    p = ll1.read();
                    c = (char) p;
                    if(isDigit(c)){
                        builder = builder + c;
                        state = 2;
                    }
                    else{
                        ll.unread(p);
                        state = 4;
                    }
                    break;
                case 3:
                    p = ll1.read();
                    c = (char) p;
                    if(c == '.'){
                        builder = builder + c;
                        state = 2;
                    }
                    else if(isWhitespace(c)){
                        ll1.unread(p);
                        ll = ll1;
                        state = 4;
                    }
                    else{
                        state = 5;
                    }
                    break;
                case 4:
                    return new Token(builder, TokenKind.const_num, TokenType.CONST_INT);
                case 5:
                    ll1.unread(p);
                    ll = ll1;
                    stopP = true;
                    return new Token("ERROR", TokenKind.ERROR, TokenType.ERROR);
            }
        }
    }
    public Token getComments() throws Exception{
        PushbackInputStream ll1 = ll;
        int state = 1; 
        int p = 0;
        char c;
        String builder = "";
        //boolean totalWhite = true;
        //Outerloop:
        while(true){
            switch (state){
                case 1:
                    p = ll1.read();
                    c = (char) p;
                    if(c == ':'){
                        builder = builder + c;
                        //System.out.println(builder);
                        p = ll1.read();
                        c = (char) p;
                        if(c == ':') {
                            builder = builder + c;
                            //System.out.println(builder);
                            state = 2;
                        }
                        else state = 9;
                    }
                    else if(c == '#'){
                        builder = builder + c;
                        p = ll1.read();
                        c = (char) p;
                        if(c == ':'){
                            builder = builder + c;
                            state = 3;
                        } 
                        else state = 9;
                    }
                    else state = 9;
                    break;
                case 2:
                    p = ll1.read();
                    c = (char) p;
                    if(p != 10){
                        builder = builder + c;
                        //System.out.println(builder);
                        state = 2;
                    }
                    else state = 7;
                    break;
                case 3: 
                    p = ll1.read();
                    c = (char) p;
                    //System.out.println(c);
                    if(c != ':') {
                        builder = builder + c;
                        state = 3;
                    }
                    else if(c == ':'){
                        p = ll1.read();
                        c = (char) p;
                        builder = builder + c;
                        if(c != '#'){
                            state = 3;
                        }
                        else if(c == '#'){
                            state = 8;
                        }
                        
                    }
                    else state = 9;
                    break;
                case 7:
                    state = 10;
                    break;
                case 8:
                    return new Token(builder, TokenKind.block_comment, TokenType.COMMENT);
                case 9:
                    //System.out.println(tR.mark(p));
                    ll1.unread(p);
                    ll = ll1;
                    stopP = true;
                    return new Token("ERROR AT LINE: " + ctr + " UNKNOWN COMMENT FORMAT", TokenKind.ERROR, TokenType.ERROR);
                case 10:
                    return new Token("line_comment", TokenKind.line_comment, TokenType.COMMENT);
                    
            }
            //break Outerloop;
        }
        //return null;
    }
    public Token checkForLeft() throws Exception{
        PushbackInputStream ll1 = ll;
        int state = 1; 
        int p = 0;
        char c;
        String builder = "";
        while(true){
            switch(state){
                case 1:
                    p = ll1.read();
                    c = (char) p;
                    if(c == '('){
                        builder = builder + c;
                        state = 2;
                    }
                    else if(c == '{'){
                        builder = builder + c;
                        state = 3;
                    }
                    else if(c == '['){
                        builder = builder + c;
                        state = 4;
                    }
                    else{
                        builder = builder + c;
                        state = 5;
                    }
                case 2:
                    ll = ll1;
                    return new Token(builder, TokenKind.LPAR, TokenType.SYMBOL);
                case 3:
                    ll = ll1;
                    return new Token(builder, TokenKind.LPAR, TokenType.SYMBOL);
                case 4:
                    ll = ll1;
                    return new Token(builder, TokenKind.LPAR, TokenType.SYMBOL);
                case 5:
                    ll1.unread(p);
                    stopP = true;
                    return new Token("ERROR AT LINE: " + ctr + " UNKNOWN ENCLOSURE", TokenKind.ERROR, TokenType.ERROR);
            }
        }
    }
    public Token checkForRight() throws Exception{
        PushbackInputStream ll1 = ll;
        int state = 1; 
        int p = 0;
        char c;
        String builder = "";
        while(true){
            switch(state){
                case 1:
                    p = ll1.read();
                    c = (char) p;
                    if(c == ')'){
                        builder = builder + c;
                        state = 2;
                    }
                    else if(c == '}'){
                        builder = builder + c;
                        state = 3;
                    }
                    else if(c == ']'){
                        builder = builder + c;
                        state = 4;
                    }
                    else{
                        builder = builder + c;
                        state = 5;
                    }
                case 2:
                    ll = ll1;
                    return new Token(builder, TokenKind.RPAR, TokenType.SYMBOL);
                case 3:
                    ll = ll1;
                    return new Token(builder, TokenKind.RPAR, TokenType.SYMBOL);
                case 4:
                    ll = ll1;
                    return new Token(builder, TokenKind.RPAR, TokenType.SYMBOL);
                case 5:
                    ll1.unread(p);
                    stopP = true;
                    return new Token("ERROR AT LINE: " + ctr + " UNKNOWN ENCLOSURE", TokenKind.ERROR, TokenType.ERROR);
            }
        }
    }
    /*public boolean isCharacter(char c){
        String s = "abcdefghijklmnopqrstuvwxyz";
        String a = "" + c;
        if(s.contains(a)){
            return true;
        }
        else return false;
    }
    public boolean isNumber(char c){
        String s = "1234567890";
        String a = "" + c;
        if(s.contains(a)) return true;
        else return false;
    }
    public boolean isID(char c){
        String s = "$";
        String a = "" + c;
        if(s.equals(a)) return true;
        else return false;
    }*/
    static void mapSetter(){
        HashMap<String, Token> rw = new HashMap<>();
        HashMap<String, Token> id = new HashMap<>();
        //tokenReader = new Scanner(new FileReader("src/emp.txt"));
        rw.put("SUNRISE", new Token("SUNRISE", TokenKind.start,TokenType.RESERVED));
        rw.put("FARMIN", new Token("FARMIN", TokenKind.start_main,TokenType.RESERVED));
        rw.put("SUNSET", new Token("SUNSET", TokenKind.end,TokenType.RESERVED));
        rw.put("FARMOUT", new Token("FARMOUT", TokenKind.end_main,TokenType.RESERVED));
        rw.put("IS", new Token("IS", TokenKind.assign_Op,TokenType.RESERVED));
        rw.put("ISEQUAL", new Token("ISEQUAL", TokenKind.relop,TokenType.RESERVED));
        rw.put("ISNOTEQUAL", new Token("ISNOTEQUAL", TokenKind.relop,TokenType.RESERVED));
        rw.put("IF", new Token("IF", TokenKind.cond_if,TokenType.RESERVED));
        rw.put("ELSE", new Token("ELSE", TokenKind.cond_else,TokenType.RESERVED));
        rw.put("ELSEIF", new Token("ELSEIF", TokenKind.cond_elseif,TokenType.RESERVED));
        rw.put("THEN", new Token("THEN", TokenKind.cond_then,TokenType.RESERVED));
        rw.put("FERTILIZEINT", new Token("FERTILIZEINT", TokenKind.input_type,TokenType.RESERVED));
        rw.put("FERTILIZEDEC", new Token("FERTILIZEDEC", TokenKind.input_type,TokenType.RESERVED));
        rw.put("FERTILIZESTRING", new Token("FERTILIZESTRING", TokenKind.input_type,TokenType.RESERVED));
        rw.put("FERTILIZEBOOL", new Token("FERTILIZEBOOL", TokenKind.input_type,TokenType.RESERVED));
        rw.put("PLANTINT", new Token("PLANTINT", TokenKind.data_type_int,TokenType.RESERVED));
        rw.put("PLANTDEC", new Token("PLANTDEC", TokenKind.data_type_double,TokenType.RESERVED));
        rw.put("PLANTSTRING", new Token("PLANTSTRING", TokenKind.data_type_string,TokenType.RESERVED));
        rw.put("PLANTBOOL", new Token("PLANTBOOL", TokenKind.data_type_bool,TokenType.RESERVED));
        rw.put("PLOW", new Token("PLOW", TokenKind.loop_do,TokenType.RESERVED));
        rw.put("WHILE", new Token("WHILE", TokenKind.loop_while,TokenType.RESERVED));
        rw.put("HARVEST", new Token("HARVEST", TokenKind.output_type,TokenType.RESERVED));
        rw.put("GROW", new Token("GROW", TokenKind.increment,TokenType.RESERVED));
        rw.put("TRIM", new Token("TRIM", TokenKind.decrement,TokenType.RESERVED));
        rw.put("AND", new Token("AND", TokenKind.relop,TokenType.RESERVED));
        rw.put("OR", new Token("OR", TokenKind.relop,TokenType.RESERVED));
        rw.put("NOT", new Token("NOT", TokenKind.relop,TokenType.RESERVED));
        rw.put("CHANGE", new Token("CHANGE", TokenKind.case_cond,TokenType.RESERVED));
        rw.put("SEASON", new Token("SEASON", TokenKind.case_cond,TokenType.RESERVED));
        rw.put("YES", new Token("YES", TokenKind.bool_const,TokenType.RESERVED));
        rw.put("NO", new Token("NO", TokenKind.bool_const,TokenType.RESERVED));
        
        rw.put("(:", new Token("BLOCK COMMENT", TokenKind.block_comment,TokenType.COMMENT));
        rw.put(":)", new Token("BLOCK COMMENT", TokenKind.block_comment,TokenType.COMMENT));
        rw.put("::", new Token("LINE COMMENT", TokenKind.line_comment,TokenType.COMMENT));
        rw.put("(", new Token("LPAR", TokenKind.LPAR,TokenType.SYMBOL));
        rw.put(")", new Token("RPAR", TokenKind.RPAR,TokenType.SYMBOL));
        rw.put("+", new Token("ARITHAS", TokenKind.ArithAS,TokenType.SYMBOL));
        rw.put("-", new Token("ARITHAS", TokenKind.ArithAS,TokenType.SYMBOL));
        rw.put("*", new Token("ARITHMD", TokenKind.ArithMD,TokenType.SYMBOL));
        rw.put("/", new Token("ARITHMD", TokenKind.ArithMD,TokenType.SYMBOL));
        
        rw.put("<", new Token("RELOP", TokenKind.relop,TokenType.RELOP));
        rw.put(">", new Token("RELOP", TokenKind.relop,TokenType.RELOP));
        rw.put("<=", new Token("RELOP", TokenKind.relop,TokenType.RELOP));
        rw.put(">=", new Token("RELOP", TokenKind.relop,TokenType.RELOP));
        rw.put(";", new Token("TERMINATOR", TokenKind.terminator,TokenType.EOF));
        rw.put("\"", new Token("STRING_FORMAT", TokenKind.string_format,TokenType.STRING_CONST));
        rw.put(".", new Token("STRING_CONCAT", TokenKind.string_concat,TokenType.STRING_CONST));
        rw.put("@n", new Token("NEW_LINE", TokenKind.white_space,TokenType.WHITESPACE));
        rw.put("@t", new Token("TAB_LINE", TokenKind.white_space,TokenType.WHITESPACE));
        rw.put(" ", new Token("WHITE_SPACE", TokenKind.white_space,TokenType.WHITESPACE));
        rw.put("@\"", new Token("DOUBLE_QUOTE", TokenKind.new_quote,TokenType.SYMBOL));
        reservedWord.putAll(rw);
    }
    /*public static void main(String[] args) throws Exception{
        
        HashMap<String, Token> rw = new HashMap<>();
        HashMap<String, Token> id = new HashMap<>();
        //tokenReader = new Scanner(new FileReader("src/emp.txt"));
        rw.put("SUNRISE", new Token("SUNRISE", TokenKind.start,TokenType.RESERVED));
        rw.put("FARMIN", new Token("FARMIN", TokenKind.start_main,TokenType.RESERVED));
        rw.put("SUNSET", new Token("SUNSET", TokenKind.end,TokenType.RESERVED));
        rw.put("FARMOUT", new Token("FARMOUT", TokenKind.end_main,TokenType.RESERVED));
        rw.put("IS", new Token("IS", TokenKind.assign_Op,TokenType.RESERVED));
        rw.put("ISEQUAL", new Token("ISEQUAL", TokenKind.relop,TokenType.RESERVED));
        rw.put("ISNOTEQUAL", new Token("ISNOTEQUAL", TokenKind.relop,TokenType.RESERVED));
        rw.put("IF", new Token("IF", TokenKind.cond_if,TokenType.RESERVED));
        rw.put("ELSE", new Token("ELSE", TokenKind.cond_else,TokenType.RESERVED));
        rw.put("ELSEIF", new Token("ELSEIF", TokenKind.cond_elseif,TokenType.RESERVED));
        rw.put("THEN", new Token("THEN", TokenKind.cond_then,TokenType.RESERVED));
        rw.put("FERTILIZEINT", new Token("FERTILIZEINT", TokenKind.input_type,TokenType.RESERVED));
        rw.put("FERTILIZEDEC", new Token("FERTILIZEDEC", TokenKind.input_type,TokenType.RESERVED));
        rw.put("FERTILIZESTRING", new Token("FERTILIZESTRING", TokenKind.input_type,TokenType.RESERVED));
        rw.put("FERTILIZEBOOL", new Token("FERTILIZEBOOL", TokenKind.input_type,TokenType.RESERVED));
        rw.put("PLANTINT", new Token("PLANTINT", TokenKind.data_type_int,TokenType.RESERVED));
        rw.put("PLANTDEC", new Token("PLANTDEC", TokenKind.data_type_double,TokenType.RESERVED));
        rw.put("PLANTSTRING", new Token("PLANTSTRING", TokenKind.data_type_string,TokenType.RESERVED));
        rw.put("PLANTBOOL", new Token("PLANTBOOL", TokenKind.data_type_bool,TokenType.RESERVED));
        rw.put("PLOW", new Token("PLOW", TokenKind.loop_do,TokenType.RESERVED));
        rw.put("WHILE", new Token("WHILE", TokenKind.loop_while,TokenType.RESERVED));
        rw.put("HARVEST", new Token("HARVEST", TokenKind.output_type,TokenType.RESERVED));
        rw.put("GROW", new Token("GROW", TokenKind.increment,TokenType.RESERVED));
        rw.put("TRIM", new Token("TRIM", TokenKind.decrement,TokenType.RESERVED));
        rw.put("AND", new Token("AND", TokenKind.relop,TokenType.RESERVED));
        rw.put("OR", new Token("OR", TokenKind.relop,TokenType.RESERVED));
        rw.put("NOT", new Token("NOT", TokenKind.relop,TokenType.RESERVED));
        rw.put("CHANGE", new Token("CHANGE", TokenKind.case_cond,TokenType.RESERVED));
        rw.put("SEASON", new Token("SEASON", TokenKind.case_cond,TokenType.RESERVED));
        rw.put("YES", new Token("YES", TokenKind.bool_const,TokenType.RESERVED));
        rw.put("NO", new Token("NO", TokenKind.bool_const,TokenType.RESERVED));
        
        rw.put("(:", new Token("BLOCK COMMENT", TokenKind.block_comment,TokenType.COMMENT));
        rw.put(":)", new Token("BLOCK COMMENT", TokenKind.block_comment,TokenType.COMMENT));
        rw.put("::", new Token("LINE COMMENT", TokenKind.line_comment,TokenType.COMMENT));
        rw.put("(", new Token("LPAR", TokenKind.LPAR,TokenType.SYMBOL));
        rw.put(")", new Token("RPAR", TokenKind.RPAR,TokenType.SYMBOL));
        rw.put("+", new Token("ARITHAS", TokenKind.ArithAS,TokenType.SYMBOL));
        rw.put("-", new Token("ARITHAS", TokenKind.ArithAS,TokenType.SYMBOL));
        rw.put("*", new Token("ARITHMD", TokenKind.ArithMD,TokenType.SYMBOL));
        rw.put("/", new Token("ARITHMD", TokenKind.ArithMD,TokenType.SYMBOL));
        
        rw.put("<", new Token("RELOP", TokenKind.relop,TokenType.RELOP));
        rw.put(">", new Token("RELOP", TokenKind.relop,TokenType.RELOP));
        rw.put("<=", new Token("RELOP", TokenKind.relop,TokenType.RELOP));
        rw.put(">=", new Token("RELOP", TokenKind.relop,TokenType.RELOP));
        rw.put(";", new Token("TERMINATOR", TokenKind.terminator,TokenType.EOF));
        rw.put("\"", new Token("STRING_FORMAT", TokenKind.string_format,TokenType.STRING_CONST));
        rw.put(".", new Token("STRING_CONCAT", TokenKind.string_concat,TokenType.STRING_CONST));
        rw.put("@n", new Token("NEW_LINE", TokenKind.white_space,TokenType.WHITESPACE));
        rw.put("@t", new Token("TAB_LINE", TokenKind.white_space,TokenType.WHITESPACE));
        rw.put(" ", new Token("WHITE_SPACE", TokenKind.white_space,TokenType.WHITESPACE));
        rw.put("@\"", new Token("DOUBLE_QUOTE", TokenKind.new_quote,TokenType.SYMBOL));
        Anal anal = new Anal();
        anal.reader();
        System.exit(0);
    }*/
}
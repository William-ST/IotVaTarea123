package com.cursoandroid.basefases;

import org.opencv.core.Mat;
import org.opencv.imgproc.Imgproc;

public class Procesador {
    private Mat gris;
    private Mat salidaintensidad;
    private Mat salidatrlocal;
    private Mat salidabinarizacion;
    private Mat salidasegmentacion;
    private Mat salidaocr;

    public enum Salida {ENTRADA, INTENSIDAD, OPERADOR_LOCAL, BINARIZACION, SEGMENTACION, RECONOCIMIENTO}

    public enum TipoIntensidad {SIN_PROCESO, LUMINANCIA, AUMENTO_LINEAL_CONSTRASTE, EQUALIZ_HISTOGRAMA, ZONAS_ROJAS, ZONAS_VERDES}

    public enum TipoOperadorLocal {SIN_PROCESO, PASO_BAJO, PASO_ALTO, GRADIENTES, GRADIENTE_COMPONENTE_ROJO, RESIDUO_DILATACION_3_3, RESIDUO_DILATACION_11_11}

    public enum TipoBinarizacion {SIN_PROCESO, ZONAS_ROJAS, ADAPTATIVA, OUTSU}

    public enum TipoSegmentacion {SIN_PROCESO, CANDIDATOS_CIRCULOS, CANDIDATOS_CIRCULOS_ELIMINAR_ANIDADAS, CANDIDATOS_ZONAS_ROJAS, CANDIDATOS_NUMEROS}

    public enum TipoReconocimiento {SIN_PROCESO, RECONOCIMIENTO_DIGITO}

    private Salida mostrarSalida;
    private TipoIntensidad tipoIntensidad;

    private TipoOperadorLocal tipoOperadorLocal;
    private TipoBinarizacion tipoBinarizacion;
    private TipoSegmentacion tipoSegmentacion;
    private TipoReconocimiento tipoReconocimiento;
    private ProcessInterface processInterface;

    public Procesador() {
        mostrarSalida = Salida.INTENSIDAD;
        tipoIntensidad = TipoIntensidad.LUMINANCIA;
        tipoOperadorLocal = TipoOperadorLocal.SIN_PROCESO;
        tipoBinarizacion = TipoBinarizacion.SIN_PROCESO;
        tipoSegmentacion = TipoSegmentacion.SIN_PROCESO;
        tipoReconocimiento = TipoReconocimiento.SIN_PROCESO;
        salidaintensidad = new Mat();
        salidatrlocal = new Mat();
        salidabinarizacion = new Mat();
        salidasegmentacion = new Mat();
        salidaocr = new Mat();
        gris = new Mat();
    }

    public Mat procesa(Mat entrada) throws RuntimeException {
        if (mostrarSalida == Salida.ENTRADA) {
            return entrada;
        }
        // Transformación intensidad
        switch (tipoIntensidad) {
            case SIN_PROCESO:
                salidaintensidad = entrada;
                break;
            case LUMINANCIA:
                Imgproc.cvtColor(entrada, salidaintensidad, Imgproc.COLOR_RGBA2GRAY);
                break;
            case AUMENTO_LINEAL_CONSTRASTE:
                processInterface = new AumentoLinealContraste();
                processInterface.init();
                salidaintensidad = processInterface.process(entrada);
                break;
            case EQUALIZ_HISTOGRAMA:
                processInterface = new EcualizacionDeHistograma();
                processInterface.init();
                salidaintensidad = processInterface.process(entrada);
                break;
            case ZONAS_ROJAS:
                processInterface = new DeteccionZonasRojas();
                processInterface.init();
                salidaintensidad = processInterface.process(entrada);
                break;
            case ZONAS_VERDES:
                processInterface = new DeteccionZonasVerdes();
                processInterface.init();
                salidaintensidad = processInterface.process(entrada);
                break;
            default:
                salidaintensidad = entrada;
        }
        if (mostrarSalida == Salida.INTENSIDAD) {
            return salidaintensidad;
        }
        // Operador local
        switch (tipoOperadorLocal) {
            case SIN_PROCESO:
                salidatrlocal = salidaintensidad;
                break;
            case PASO_BAJO:
                processInterface = new FiltradoPasoAlto();
                processInterface.init();
                salidatrlocal = processInterface.process(entrada);
                //resultado en salidatrlocal
                break;
            case GRADIENTES:
                processInterface = new DeteccionContornosGradientesSobel();
                processInterface.init();
                salidatrlocal = processInterface.process(entrada);
                break;
            case GRADIENTE_COMPONENTE_ROJO:
                processInterface = new GradientesSobelComponenteRojo();
                processInterface.init();
                salidatrlocal = processInterface.process(entrada);
                break;
            case RESIDUO_DILATACION_3_3:
                processInterface = new GradienteMorfológicoResiduoDilatacion_3_3();
                processInterface.init();
                salidatrlocal = processInterface.process(entrada);
                break;
            case RESIDUO_DILATACION_11_11:
                processInterface = new GradienteMorfologicoResiduoDilatacion_11_11();
                processInterface.init();
                salidatrlocal = processInterface.process(entrada);
                break;
        }
        if (mostrarSalida == Salida.OPERADOR_LOCAL) {
            return salidatrlocal;
        }
        // Binarización
        switch (tipoBinarizacion) {
            case SIN_PROCESO:
                salidabinarizacion = salidatrlocal;
                break;
            case ZONAS_ROJAS:
                processInterface = new BinarizacionZonasRojas();
                processInterface.init();
                salidabinarizacion = processInterface.process(entrada);
                break;
            case ADAPTATIVA:
                processInterface = new BinarizacionAdaptativa();
                processInterface.init();
                salidabinarizacion = processInterface.process(entrada);
                break;
            case OUTSU:
                processInterface = new BinarizacionOtsu();
                processInterface.init();
                salidabinarizacion = processInterface.process(entrada);
                break;
            default:
                salidabinarizacion = salidatrlocal;
                break;
        }
        if (mostrarSalida == Salida.BINARIZACION) {
            return salidabinarizacion;
        }
        // Segmentación
        switch (tipoSegmentacion) {
            case SIN_PROCESO:
                salidasegmentacion = salidabinarizacion;
                break;
            case CANDIDATOS_CIRCULOS:
                processInterface = new SeleccionCandidatosCirculos();
                processInterface.init();
                salidasegmentacion = processInterface.process(entrada);
                break;
            case CANDIDATOS_CIRCULOS_ELIMINAR_ANIDADAS:
                processInterface = new SeleccionCandidatosCirculosEliminarDeteccionesAnidadas();
                processInterface.init();
                salidasegmentacion = processInterface.process(entrada);
                break;
            case CANDIDATOS_ZONAS_ROJAS:
                processInterface = new SeleccionCandidatosZonasRojas();
                processInterface.init();
                salidasegmentacion = processInterface.process(entrada);
                break;
            case CANDIDATOS_NUMEROS:
                processInterface = new SegmentacionDigitosDentroSenal();
                processInterface.init();
                salidasegmentacion = processInterface.process(entrada);
                break;
        }
        if (mostrarSalida == Salida.SEGMENTACION) {
            return salidasegmentacion;
        }
        // Reconocimiento OCR
        switch (tipoReconocimiento) {
            case SIN_PROCESO:
                salidaocr = salidasegmentacion;
                break;
            case RECONOCIMIENTO_DIGITO:
                processInterface = new LecturasCaracteresIndividuales();
                processInterface.init();
                processInterface.process(entrada);
                break;
        }
        return salidaocr;
    }

    public Mat getGris() {
        return gris;
    }

    public void setGris(Mat gris) {
        this.gris = gris;
    }

    public Mat getSalidaintensidad() {
        return salidaintensidad;
    }

    public void setSalidaintensidad(Mat salidaintensidad) {
        this.salidaintensidad = salidaintensidad;
    }

    public Mat getSalidatrlocal() {
        return salidatrlocal;
    }

    public void setSalidatrlocal(Mat salidatrlocal) {
        this.salidatrlocal = salidatrlocal;
    }

    public Mat getSalidabinarizacion() {
        return salidabinarizacion;
    }

    public void setSalidabinarizacion(Mat salidabinarizacion) {
        this.salidabinarizacion = salidabinarizacion;
    }

    public Mat getSalidasegmentacion() {
        return salidasegmentacion;
    }

    public void setSalidasegmentacion(Mat salidasegmentacion) {
        this.salidasegmentacion = salidasegmentacion;
    }

    public Mat getSalidaocr() {
        return salidaocr;
    }

    public void setSalidaocr(Mat salidaocr) {
        this.salidaocr = salidaocr;
    }

    public Salida getMostrarSalida() {
        return mostrarSalida;
    }

    public void setMostrarSalida(Salida mostrarSalida) {
        this.mostrarSalida = mostrarSalida;
    }

    public TipoIntensidad getTipoIntensidad() {
        return tipoIntensidad;
    }

    public void setTipoIntensidad(TipoIntensidad tipoIntensidad) {
        this.tipoIntensidad = tipoIntensidad;
    }

    public TipoOperadorLocal getTipoOperadorLocal() {
        return tipoOperadorLocal;
    }

    public void setTipoOperadorLocal(TipoOperadorLocal tipoOperadorLocal) {
        this.tipoOperadorLocal = tipoOperadorLocal;
    }

    public TipoBinarizacion getTipoBinarizacion() {
        return tipoBinarizacion;
    }

    public void setTipoBinarizacion(TipoBinarizacion tipoBinarizacion) {
        this.tipoBinarizacion = tipoBinarizacion;
    }

    public TipoSegmentacion getTipoSegmentacion() {
        return tipoSegmentacion;
    }

    public void setTipoSegmentacion(TipoSegmentacion tipoSegmentacion) {
        this.tipoSegmentacion = tipoSegmentacion;
    }

    public TipoReconocimiento getTipoReconocimiento() {
        return tipoReconocimiento;
    }

    public void setTipoReconocimiento(TipoReconocimiento tipoReconocimiento) {
        this.tipoReconocimiento = tipoReconocimiento;
    }
}

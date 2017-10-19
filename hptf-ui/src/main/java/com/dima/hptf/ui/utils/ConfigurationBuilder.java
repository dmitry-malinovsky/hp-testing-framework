package com.dima.hptf.ui.utils;


import com.google.common.base.Predicate;
import com.google.common.collect.Lists;
import com.google.common.collect.ObjectArrays;
import com.google.common.collect.Sets;
import org.reflections.Configuration;
import org.reflections.Reflections;
import org.reflections.ReflectionsException;
import org.reflections.adapters.JavaReflectionAdapter;
import org.reflections.adapters.JavassistAdapter;
import org.reflections.adapters.MetadataAdapter;
import org.reflections.scanners.Scanner;
import org.reflections.scanners.SubTypesScanner;
import org.reflections.scanners.TypeAnnotationsScanner;
import org.reflections.serializers.Serializer;
import org.reflections.serializers.XmlSerializer;
import org.reflections.util.ClasspathHelper;
import org.reflections.util.FilterBuilder;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;
import javax.print.DocFlavor;
import java.net.URL;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class ConfigurationBuilder implements Configuration {
    @Nonnull
    private Set<Scanner> scanners = Sets.newHashSet(new Scanner[]{new TypeAnnotationsScanner(), new SubTypesScanner()});
    @Nonnull
    private Set<URL> urls = Sets.newHashSet();
    protected MetadataAdapter metadataAdapter;
    @Nullable
    private Predicate<String> inputsFilter;
    private Serializer serializer;
    @Nullable
    private ExecutorService executorService;
    @Nullable
    private ClassLoader[] classLoaders;

    public ConfigurationBuilder() {
    }

    public static ConfigurationBuilder build(@Nullable Object... params) {
        ConfigurationBuilder builder = new ConfigurationBuilder();
        ArrayList parameters = Lists.newArrayList();
        Iterator var7;
        Object param;
        if(params != null) {
            Object[] loaders = params;
            int classLoaders = params.length;

            for(int filter = 0; filter < classLoaders; ++filter) {
                Object scanners = loaders[filter];
                if(scanners != null) {
                    if(scanners.getClass().isArray()) {
                        Object[] var18 = (Object[])((Object[])scanners);
                        int var19 = var18.length;

                        for(int var9 = 0; var9 < var19; ++var9) {
                            Object p = var18[var9];
                            if(p != null) {
                                parameters.add(p);
                            }
                        }
                    } else if(scanners instanceof Iterable) {
                        var7 = ((Iterable)scanners).iterator();

                        while(var7.hasNext()) {
                            param = var7.next();
                            if(param != null) {
                                parameters.add(param);
                            }
                        }
                    } else {
                        parameters.add(scanners);
                    }
                }
            }
        }

        ArrayList var12 = Lists.newArrayList();
        Iterator var13 = parameters.iterator();

        while(var13.hasNext()) {
            Object var15 = var13.next();
            if(var15 instanceof ClassLoader) {
                var12.add((ClassLoader)var15);
            }
        }

        ClassLoader[] var14 = var12.isEmpty()?null:(ClassLoader[])var12.toArray(new ClassLoader[var12.size()]);
        FilterBuilder var16 = new FilterBuilder();
        ArrayList var17 = Lists.newArrayList();
        var7 = parameters.iterator();

        while(var7.hasNext()) {
            param = var7.next();
            if(param instanceof String) {
                builder.addUrls(ClasspathHelper.forPackage((String)param, var14));
                var16.includePackage(new String[]{(String)param});
            } else if(param instanceof Class) {
                if(Scanner.class.isAssignableFrom((Class)param)) {
                    try {
                        builder.addScanners(new Scanner[]{(Scanner)((Class)param).newInstance()});
                    } catch (Exception var11) {
                        ;
                    }
                }

                builder.addUrls(new URL[]{ClasspathHelper.forClass((Class)param, var14)});
                var16.includePackage((Class)param);
            } else if(param instanceof Scanner) {
                var17.add((Scanner)param);
            } else if(param instanceof URL) {
                builder.addUrls(new URL[]{(URL)param});
            } else if(!(param instanceof ClassLoader)) {
                if(param instanceof Predicate) {
                    var16.add((Predicate)param);
                } else if(param instanceof ExecutorService) {
                    builder.setExecutorService((ExecutorService)param);
                } else if(Reflections.log != null) {
                    throw new ReflectionsException("could not use param " + param);
                }
            }
        }

        if(builder.getUrls().isEmpty()) {
            if(var14 != null) {
                builder.addUrls(ClasspathHelper.forClassLoader(var14));
            } else {
                builder.addUrls(ClasspathHelper.forClassLoader());
            }
        }

        builder.filterInputsBy(var16);
        if(!var17.isEmpty()) {
            builder.setScanners((Scanner[])var17.toArray(new Scanner[var17.size()]));
        }

        if(!var12.isEmpty()) {
            builder.addClassLoaders((Collection)var12);
        }

        return builder;
    }

    public ConfigurationBuilder forPackages(String... packages) {
        String[] var2 = packages;
        int var3 = packages.length;

        for(int var4 = 0; var4 < var3; ++var4) {
            String pkg = var2[var4];
            this.addUrls(ClasspathHelper.forPackage(pkg, new ClassLoader[0]));
        }

        return this;
    }

    @Nonnull
    public Set<Scanner> getScanners() {
        return this.scanners;
    }

    public ConfigurationBuilder setScanners(@Nonnull Scanner... scanners) {
        this.scanners.clear();
        return this.addScanners(scanners);
    }

    public ConfigurationBuilder addScanners(Scanner... scanners) {
        this.scanners.addAll(Sets.newHashSet(scanners));
        return this;
    }

    @Nonnull
    public Set<URL> getUrls() {
        return this.urls;
    }

    public ConfigurationBuilder setUrls(@Nonnull Collection<URL> urls) {
        this.urls = Sets.newHashSet(urls);
        return this;
    }

    public ConfigurationBuilder setUrls(URL... urls) {
        this.urls = Sets.newHashSet(urls);
        return this;
    }

    public ConfigurationBuilder addUrls(Collection<URL> urls) {
        this.urls.addAll(urls);
        return this;
    }

    public ConfigurationBuilder addUrls(URL... urls) {
        this.urls.addAll(Sets.newHashSet(urls));
        return this;
    }

    public MetadataAdapter getMetadataAdapter() {
        if(this.metadataAdapter != null) {
            return this.metadataAdapter;
        } else {
            try {
                return this.metadataAdapter = new JavassistAdapter();
            } catch (Throwable var2) {
                if(Reflections.log != null) {
                    Reflections.log.warn("could not create JavassistAdapter, using JavaReflectionAdapter", var2);
                }

                return this.metadataAdapter = new JavaReflectionAdapter();
            }
        }
    }

    public ConfigurationBuilder setMetadataAdapter(MetadataAdapter metadataAdapter) {
        this.metadataAdapter = metadataAdapter;
        return this;
    }

    @Nullable
    public Predicate<String> getInputsFilter() {
        return this.inputsFilter;
    }

    public void setInputsFilter(@Nullable Predicate<String> inputsFilter) {
        this.inputsFilter = inputsFilter;
    }

    public ConfigurationBuilder filterInputsBy(Predicate<String> inputsFilter) {
        this.inputsFilter = inputsFilter;
        return this;
    }

    @Nullable
    public ExecutorService getExecutorService() {
        return this.executorService;
    }

    public ConfigurationBuilder setExecutorService(@Nullable ExecutorService executorService) {
        this.executorService = executorService;
        return this;
    }

    public ConfigurationBuilder useParallelExecutor() {
        return this.useParallelExecutor(Runtime.getRuntime().availableProcessors());
    }

    public ConfigurationBuilder useParallelExecutor(int availableProcessors) {
        this.setExecutorService(Executors.newFixedThreadPool(availableProcessors));
        return this;
    }

    public Serializer getSerializer() {
        return this.serializer != null?this.serializer:(this.serializer = new XmlSerializer());
    }

    public ConfigurationBuilder setSerializer(Serializer serializer) {
        this.serializer = serializer;
        return this;
    }

    @Nullable
    public ClassLoader[] getClassLoaders() {
        return this.classLoaders;
    }

    public void setClassLoaders(@Nullable ClassLoader[] classLoaders) {
        this.classLoaders = classLoaders;
    }

    public ConfigurationBuilder addClassLoader(ClassLoader classLoader) {
        return this.addClassLoaders(new ClassLoader[]{classLoader});
    }

    public ConfigurationBuilder addClassLoaders(ClassLoader... classLoaders) {
        this.classLoaders = this.classLoaders == null?classLoaders:(ClassLoader[]) ObjectArrays.concat(this.classLoaders, classLoaders, ClassLoader.class);
        return this;
    }

    public ConfigurationBuilder addClassLoaders(Collection<ClassLoader> classLoaders) {
        return this.addClassLoaders((ClassLoader[])classLoaders.toArray(new ClassLoader[classLoaders.size()]));
    }
}
